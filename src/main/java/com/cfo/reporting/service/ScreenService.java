package com.cfo.reporting.service;


import com.cfo.reporting.model.Screen;
import com.cfo.reporting.service.config.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ScreenService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PantallaConfigRepository configRepository;
    private final CacheManager cacheManager;

    public ScreenService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.configRepository = new PantallaConfigRepository();
        this.cacheManager = new CacheManager();
    }

    public Map<String, Object> obtenerDatosPantalla(String clavePantalla) {
        return obtenerDatosPantalla(clavePantalla, null);
    }

    public Map<String, Object> obtenerDatosPantalla(String clavePantalla, Map<String, Object> parametrosUsuario) {
        PantallaConfig config = configRepository.obtenerConfiguracion(clavePantalla);
        Map<String, Object> resultados = new HashMap<>();

        for (ConsultaConfig consultaConfig : config.getConsultas()) {
            // Validar parámetros para esta consulta
            consultaConfig.validarParametros(parametrosUsuario);

            // Preparar parámetros con valores por defecto
            Map<String, Object> parametros = consultaConfig.prepararParametros(parametrosUsuario);

            // Generar clave única para la caché
            String claveCache = generarClaveCache(clavePantalla, consultaConfig.getNombre(), parametros);

            // Inicializar caché si no existe
            if (!cacheExiste(claveCache)) {
                cacheManager.inicializarCacheParaConsulta(claveCache, consultaConfig);
            }

            // Ejecutar la consulta y obtener resultados
            Object resultadoConsulta = ejecutarConsulta(consultaConfig, parametros, claveCache);
            resultados.put(consultaConfig.getNombre(), resultadoConsulta);
        }

        return resultados;
    }

    private Object ejecutarConsulta(ConsultaConfig consultaConfig, Map<String, Object> parametros, String claveCache) {
        String tipoResultado = consultaConfig.getTipoResultado();

        switch (tipoResultado.toUpperCase()) {
            case "MAPA":
                return ejecutarConsultaMapa(consultaConfig, parametros, claveCache);
            case "LISTA_MAPAS":
                return ejecutarConsultaListaMapas(consultaConfig, parametros, claveCache);
            case "LISTA_OBJETOS":
                return ejecutarConsultaListaObjetos(consultaConfig, parametros);
            default:
                throw new RuntimeException("Tipo de resultado no soportado: " + tipoResultado);
        }
    }

    private Map<Object, Object> ejecutarConsultaMapa(ConsultaConfig consultaConfig, Map<String, Object> parametros, String claveCache) {
        // Primero intentar obtener de la caché completa
        Map<Object, Object> cacheCompleto = cacheManager.obtenerCache(claveCache);
        if (!cacheCompleto.isEmpty()) {
            return new HashMap<>(cacheCompleto);
        }

        // Si la caché está vacía, ejecutar la query y poblar la caché
        Map<Object, Object> resultado = new HashMap<>();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(consultaConfig.getQuery(), parametros);
        while (rowSet.next()) {
            Object clave = rowSet.getObject(consultaConfig.getClaveCache());
            Object valor = rowSet.getObject(consultaConfig.getValorCache());

            resultado.put(clave, valor);
            cacheManager.ponerEnCache(claveCache, clave, valor);
        }

        return resultado;
    }

    private List<Map<String, Object>> ejecutarConsultaListaMapas(ConsultaConfig consultaConfig, Map<String, Object> parametros, String claveCache) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(consultaConfig.getQuery(), parametros);
        while (rowSet.next()) {
            Map<String, Object> fila = new HashMap<>();
            for (int i = 1; i <= rowSet.getMetaData().getColumnCount(); i++) {
                String nombreColumna = rowSet.getMetaData().getColumnName(i);
                fila.put(nombreColumna, rowSet.getObject(i));
            }
            resultados.add(fila);

            // Si hay configuración para caché de mapa, almacenar también en caché
            if (consultaConfig.getClaveCache() != null && consultaConfig.getValorCache() != null) {
                Object clave = fila.get(consultaConfig.getClaveCache());
                Object valor = fila.get(consultaConfig.getValorCache());
                cacheManager.ponerEnCache(claveCache, clave, valor);
            }
        }

        return resultados;
    }

    private List<Object> ejecutarConsultaListaObjetos(ConsultaConfig consultaConfig, Map<String, Object> parametros) {
        List<Object> resultados = new ArrayList<>();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(consultaConfig.getQuery(), parametros);
        while (rowSet.next()) {
            // Para consultas de una sola columna
            if (rowSet.getMetaData().getColumnCount() == 1) {
                resultados.add(rowSet.getObject(1));
            } else {
                // Para múltiples columnas, devolver un mapa
                Map<String, Object> fila = new HashMap<>();
                for (int i = 1; i <= rowSet.getMetaData().getColumnCount(); i++) {
                    String nombreColumna = rowSet.getMetaData().getColumnName(i);
                    fila.put(nombreColumna, rowSet.getObject(i));
                }
                resultados.add(fila);
            }
        }

        return resultados;
    }

    private String generarClaveCache(String clavePantalla, String nombreConsulta, Map<String, Object> parametros) {
        StringBuilder claveBuilder = new StringBuilder(clavePantalla).append("_").append(nombreConsulta);

        if (parametros != null && !parametros.isEmpty()) {
            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                claveBuilder.append("_")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }
        }

        return claveBuilder.toString();
    }

    private boolean cacheExiste(String claveCache) {
        return cacheManager.obtenerCache(claveCache) != null;
    }

    public Object obtenerDatosConsulta(String clavePantalla, String nombreConsulta) {
        return obtenerDatosConsulta(clavePantalla, nombreConsulta, null);
    }

    public Object obtenerDatosConsulta(String clavePantalla, String nombreConsulta, Map<String, Object> parametrosUsuario) {
        ConsultaConfig consultaConfig = configRepository.obtenerConsultaConfig(clavePantalla, nombreConsulta);

        // Validar parámetros
        consultaConfig.validarParametros(parametrosUsuario);

        // Preparar parámetros con valores por defecto
        Map<String, Object> parametros = consultaConfig.prepararParametros(parametrosUsuario);

        // Generar clave única para la caché
        String claveCache = generarClaveCache(clavePantalla, nombreConsulta, parametros);

        // Inicializar caché si no existe
        if (!cacheExiste(claveCache)) {
            cacheManager.inicializarCacheParaConsulta(claveCache, consultaConfig);
        }

        // Ejecutar la consulta y obtener resultados
        return ejecutarConsulta(consultaConfig, parametros, claveCache);
    }

    public void limpiarCacheConsulta(String clavePantalla, String nombreConsulta) {
        limpiarCacheConsulta(clavePantalla, nombreConsulta, null);
    }

    public void limpiarCacheConsulta(String clavePantalla, String nombreConsulta, Map<String, Object> parametros) {
        ConsultaConfig consultaConfig = configRepository.obtenerConsultaConfig(clavePantalla, nombreConsulta);
        Map<String, Object> parametrosPreparados = consultaConfig.prepararParametros(parametros);
        String claveCache = generarClaveCache(clavePantalla, nombreConsulta, parametrosPreparados);

        cacheManager.limpiarCache(claveCache);
    }

    public void actualizarCacheConsulta(String clavePantalla, String nombreConsulta) {
        actualizarCacheConsulta(clavePantalla, nombreConsulta, null);
    }

    public void actualizarCacheConsulta(String clavePantalla, String nombreConsulta, Map<String, Object> parametros) {
        limpiarCacheConsulta(clavePantalla, nombreConsulta, parametros);
        obtenerDatosConsulta(clavePantalla, nombreConsulta, parametros);
    }


}
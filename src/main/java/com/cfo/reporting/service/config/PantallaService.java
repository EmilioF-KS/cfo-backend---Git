package com.cfo.reporting.service.config;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class PantallaService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PantallaConfigRepository configRepository;
    private final CacheManager cacheManager;

    public PantallaService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.configRepository = new PantallaConfigRepository();
        this.cacheManager = new CacheManager();
    }

    public Map<String, Object> obtenerDatosPantalla(String clavePantalla, Map<String, Object> parametrosUsuario) {
        PantallaConfig config = configRepository.obtenerConfiguracion(clavePantalla);
        Map<String, Object> resultados = new HashMap<>();

        for (ConsultaConfig consultaConfig : config.getConsultas()) {
            try {
                // Validar parámetros para esta consulta
                consultaConfig.validarParametros(parametrosUsuario);

                // Preparar parámetros con valores por defecto
                Map<String, Object> parametros = consultaConfig.prepararParametros(parametrosUsuario);

                // Validar la query antes de ejecutarla
                QueryFormatter.validateQuery(consultaConfig.getQuery());

                // Generar clave única para la caché
                String claveCache = generarClaveCache(clavePantalla, consultaConfig.getNombre(), parametros);

                // Inicializar caché si no existe
                if (!cacheExiste(claveCache)) {
                    cacheManager.inicializarCacheParaConsulta(claveCache, consultaConfig);
                }

                // Ejecutar la consulta y obtener resultados
                Object resultadoConsulta = ejecutarConsulta(consultaConfig, parametros, claveCache);
                resultados.put(consultaConfig.getNombre(), resultadoConsulta);
            } catch (Exception e) {
                // Registrar el error pero continuar con otras consultas
                resultados.put(consultaConfig.getNombre(), "Error: " + e.getMessage());
                // También podrías lanzar una excepción personalizada aquí
            }
        }

        return resultados;
    }



    private Object ejecutarConsulta(ConsultaConfig consultaConfig, Map<String, Object> parametros, String claveCache) {
        String tipoResultado = consultaConfig.getTipoResultado();

        try {
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
        } catch (DataAccessException e) {
            // Mejorar el mensaje de error para ayudar en la depuración
            String queryFormateada = QueryFormatter.formatQuery(consultaConfig.getQuery(), parametros);
            throw new RuntimeException("Error al ejecutar la query: " + queryFormateada + ". Error: " + e.getMessage(), e);
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

        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(consultaConfig.getQuery(), parametros);
            while (rowSet.next()) {
                Object clave = rowSet.getObject(consultaConfig.getClaveCache());
                Object valor = rowSet.getObject(consultaConfig.getValorCache());

                resultado.put(clave, valor);
                cacheManager.ponerEnCache(claveCache, clave, valor);
            }
        } catch (Exception e) {
            // Formatear la query para ayudar en la depuración
            String queryFormateada = QueryFormatter.formatQuery(consultaConfig.getQuery(), parametros);
            throw new RuntimeException("Error en ejecutarConsultaMapa con query: " + queryFormateada, e);
        }

        return resultado;
    }

    private List<Map<String, Object>> ejecutarConsultaListaMapas(ConsultaConfig consultaConfig, Map<String, Object> parametros, String claveCache) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        try {
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
        } catch (Exception e) {
            String queryFormateada = QueryFormatter.formatQuery(consultaConfig.getQuery(), parametros);
            throw new RuntimeException("Error en ejecutarConsultaListaMapas con query: " + queryFormateada, e);
        }

        return resultados;
    }

    private List<Object> ejecutarConsultaListaObjetos(ConsultaConfig consultaConfig, Map<String, Object> parametros) {
        List<Object> resultados = new ArrayList<>();

        try {
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
        } catch (Exception e) {
            String queryFormateada = QueryFormatter.formatQuery(consultaConfig.getQuery(), parametros);
            throw new RuntimeException("Error en ejecutarConsultaListaObjetos con query: " + queryFormateada, e);
        }

        return resultados;
    }

    private String generarClaveCache(String clavePantalla, String nombreConsulta, Map<String, Object> parametros) {
        StringBuilder claveBuilder = new StringBuilder(clavePantalla)
                .append("_")
                .append(nombreConsulta);

        if (parametros != null && !parametros.isEmpty()) {
            // Ordenar las claves para asegurar consistencia sin importar el orden de los parámetros
            parametros.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        claveBuilder.append("_")
                                .append(entry.getKey())
                                .append("=")
                                .append(entry.getValue());
                    });
        }

        return claveBuilder.toString();
    }
    private boolean cacheExiste(String claveCache) {
        try {
            Map<Object, Object> cache = cacheManager.obtenerCache(claveCache);
            return cache != null && !cache.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }//

    public Map<String, Map<String, Object>> obtenerDatosConsulta(String clavePantalla, String nombreConsulta) {
        return obtenerDatosConsulta(clavePantalla, nombreConsulta, null);
    }

    public Map<String, Map<String, Object>> obtenerDatosConsulta(String clavePantalla, String nombreConsulta, Map<String, Object> parametrosUsuario) {
        Map<String, Map<String, Object>> resultadoFinal = new HashMap<>();

        try {
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
            Map<String, Object> datosConsulta = ejecutarConsultaParaMapa(consultaConfig, parametros, claveCache);

            // Agregar al resultado final con el nombre de la consulta como clave
            resultadoFinal.put(nombreConsulta, datosConsulta);

        } catch (Exception e) {
            // En caso de error, agregar un mapa con información del error
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            resultadoFinal.put(nombreConsulta, errorMap);
        }

        return resultadoFinal;
    }

    private Map<String, Object> ejecutarConsultaParaMapa(ConsultaConfig consultaConfig, Map<String, Object> parametros, String claveCache) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(consultaConfig.getQuery(), parametros);

            // Obtener información de las columnas
            int columnCount = rowSet.getMetaData().getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(rowSet.getMetaData().getColumnName(i));
            }

            // Procesar cada fila
            while (rowSet.next()) {
                resultado.put(String.valueOf(rowSet.getObject(1)),
                rowSet.getObject(2));
            }
        } catch (Exception e) {
            String queryFormateada = QueryFormatter.formatQuery(consultaConfig.getQuery(), parametros);
            throw new RuntimeException("Error en ejecutarConsultaParaMapa con query: " + queryFormateada, e);
        }

        return resultado;
    }

    public Map<String, Map<String, Object>> obtenerMultiplesConsultas(String clavePantalla, List<String> nombresConsultas) {
        return obtenerMultiplesConsultas(clavePantalla, nombresConsultas, null);
    }

    public Map<String, Map<String, Object>> obtenerMultiplesConsultas(String clavePantalla, List<String> nombresConsultas, Map<String, Object> parametrosUsuario) {
        Map<String, Map<String, Object>> resultados = new HashMap<>();

        for (String nombreConsulta : nombresConsultas) {
            Map<String, Map<String, Object>> resultadoConsulta = obtenerDatosConsulta(clavePantalla, nombreConsulta, parametrosUsuario);
            resultados.putAll(resultadoConsulta);
        }

        return resultados;
    }

    public Map<String, Map<String, Object>> obtenerTodasConsultasPantalla(String clavePantalla) {
        return obtenerTodasConsultasPantalla(clavePantalla, null);
    }

    public Map<String, Map<String, Object>> obtenerTodasConsultasPantalla(String clavePantalla, Map<String, Object> parametrosUsuario) {
        PantallaConfig config = configRepository.obtenerConfiguracion(clavePantalla);
        Map<String, Map<String, Object>> resultados = new HashMap<>();

        for (ConsultaConfig consultaConfig : config.getConsultas()) {
            Map<String, Map<String, Object>> resultadoConsulta =
                    obtenerDatosConsulta(clavePantalla, consultaConfig.getNombre(), parametrosUsuario);
            resultados.putAll(resultadoConsulta);
        }

        return resultados;
    }
}

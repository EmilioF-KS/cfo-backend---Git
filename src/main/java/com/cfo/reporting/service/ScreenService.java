package com.cfo.reporting.service;


import com.cfo.reporting.service.config.CacheManager;
import com.cfo.reporting.service.config.ScreenConfig;
import com.cfo.reporting.service.config.ScreenConfigRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.HashMap;
import java.util.Map;

public class ScreenService {
    private final JdbcTemplate jdbcTemplate;
    private final ScreenConfigRepository configRepository;
    private final CacheManager cacheManager;

    public ScreenService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.configRepository = new ScreenConfigRepository();
        this.cacheManager = new CacheManager();

        // Inicializar caches para todas las configuraciones
        inicializarCaches();
    }

    private void inicializarCaches() {
        Map<String, ScreenConfig> configuraciones = configRepository.obtenerTodasConfiguraciones();
        for (ScreenConfig config : configuraciones.values()) {
            cacheManager.inicializarCacheParaPantalla(config);
        }
    }

    public Map<Object, Object> obtenerDatosPantalla(String clavePantalla) {
        // Primero intentar obtener de la caché
        Map<Object, Object> cacheCompleto = cacheManager.obtenerCache(clavePantalla);
        if (!cacheCompleto.isEmpty()) {
            return new HashMap<>(cacheCompleto);
        }

        // Si la caché está vacía, ejecutar la query y poblar la caché
        ScreenConfig config = configRepository.obtenerConfiguracion(clavePantalla);
        Map<Object, Object> resultado = new HashMap<>();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(config.getQuery());
        while (rowSet.next()) {
            Object clave = rowSet.getObject(config.getClaveCache());
            Object valor = rowSet.getObject(config.getValorCache());

            resultado.put(clave, valor);
            cacheManager.ponerEnCache(clavePantalla, clave, valor);
        }

        return resultado;
    }

    public Object obtenerValorDeCache(String clavePantalla, Object clave) {
        return cacheManager.obtenerDeCache(clavePantalla, clave);
    }

    public void limpiarCachePantalla(String clavePantalla) {
        cacheManager.limpiarCache(clavePantalla);
    }

    public void actualizarCachePantalla(String clavePantalla) {
        limpiarCachePantalla(clavePantalla);
        obtenerDatosPantalla(clavePantalla); // Esto volverá a poblar la caché
    }
}
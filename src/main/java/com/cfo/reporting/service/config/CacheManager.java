package com.cfo.reporting.service.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private final Map<String, Cache<Object, Object>> caches = new ConcurrentHashMap<>();

    public void inicializarCacheParaPantalla(ScreenConfig config) {
        Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(config.getTiempoExpiracion(), config.getUnidadTiempo())
                .maximumSize(1000)
                .build();

        caches.put(config.getClavePantalla(), cache);
    }

    public Map<Object, Object> obtenerCache(String clavePantalla) {
        Cache<Object, Object> cache = caches.get(clavePantalla);
        if (cache == null) {
            throw new RuntimeException("Cache no inicializado para la pantalla: " + clavePantalla);
        }

        return cache.asMap();
    }

    public void ponerEnCache(String clavePantalla, Object clave, Object valor) {
        Cache<Object, Object> cache = caches.get(clavePantalla);
        if (cache != null) {
            cache.put(clave, valor);
        }
    }

    public Object obtenerDeCache(String clavePantalla, Object clave) {
        Cache<Object, Object> cache = caches.get(clavePantalla);
        return cache != null ? cache.getIfPresent(clave) : null;
    }

    public void limpiarCache(String clavePantalla) {
        Cache<Object, Object> cache = caches.get(clavePantalla);
        if (cache != null) {
            cache.invalidateAll();
        }
    }
}
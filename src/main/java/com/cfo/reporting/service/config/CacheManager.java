package com.cfo.reporting.service.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheManager {
    private final Map<String, Cache<Object, Object>> caches = new ConcurrentHashMap<>();

    public void inicializarCacheParaConsulta(String claveCache, ConsultaConfig config) {
        ConfigCache configCache = config.getConfigCache();
        Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(configCache.getTiempoExpiracion(), configCache.getUnidadTiempo())
                .maximumSize(1000)
                .build();

        caches.put(claveCache, cache);
    }

    public Map<Object, Object> obtenerCache(String claveCache) {
        Cache<Object, Object> cache = caches.get(claveCache);
        if (cache == null) {
            // Si no existe, crear una nueva caché con configuración por defecto
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    .maximumSize(1000)
                    .build();
            caches.put(claveCache, cache);
        }

        return cache.asMap();
    }

    public void ponerEnCache(String claveCache, Object clave, Object valor) {
        Cache<Object, Object> cache = caches.get(claveCache);
        if (cache == null) {
            // Si no existe, crear una nueva caché con configuración por defecto
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    .maximumSize(1000)
                    .build();
            caches.put(claveCache, cache);
        }

        cache.put(clave, valor);
    }

    public Object obtenerDeCache(String claveCache, Object clave) {
        Cache<Object, Object> cache = caches.get(claveCache);
        return cache != null ? cache.getIfPresent(clave) : null;
    }

    public void limpiarCache(String claveCache) {
        Cache<Object, Object> cache = caches.get(claveCache);
        if (cache != null) {
            cache.invalidateAll();
        }
    }
}
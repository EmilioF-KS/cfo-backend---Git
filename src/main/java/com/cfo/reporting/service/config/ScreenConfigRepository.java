package com.cfo.reporting.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ScreenConfigRepository {
    private final Map<String, ScreenConfig> configuraciones = new HashMap<>();

    public ScreenConfigRepository() {
        cargarConfiguraciones();
    }

    private void cargarConfiguraciones() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("screens-config.json");

            if (inputStream == null) {
                throw new RuntimeException("Archivo de configuración no encontrado");
            }

            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode pantallasNode = rootNode.get("pantallas");

            for (JsonNode pantallaNode : pantallasNode) {
                ScreenConfig config = objectMapper.treeToValue(pantallaNode, ScreenConfig.class);
                configuraciones.put(config.getClavePantalla(), config);
            }

            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar configuraciones: " + e.getMessage(), e);
        }
    }

    public ScreenConfig obtenerConfiguracion(String clavePantalla) {
        ScreenConfig config = configuraciones.get(clavePantalla);
        if (config == null) {
            throw new RuntimeException("Configuración no encontrada para la pantalla: " + clavePantalla);
        }
        return config;
    }

    public Map<String, ScreenConfig> obtenerTodasConfiguraciones() {
        return new HashMap<>(configuraciones);
    }
}
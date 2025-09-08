package com.cfo.reporting.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class PantallaConfigRepository {
    private final Map<String, PantallaConfig> configuraciones = new HashMap<>();
//
//    @Value("${spring.screen.config.file}")
//    private String fileConfiguration;

    public PantallaConfigRepository() {
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
                PantallaConfig config = objectMapper.treeToValue(pantallaNode, PantallaConfig.class);
                configuraciones.put(config.getClavePantalla(), config);
            }

            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar configuraciones: " + e.getMessage(), e);
        }
    }

    public PantallaConfig obtenerConfiguracion(String clavePantalla) {
        PantallaConfig config = configuraciones.get(clavePantalla);
        if (config == null) {
            throw new RuntimeException("Configuración no encontrada para la pantalla: " + clavePantalla);
        }
        return config;
    }

    public ConsultaConfig obtenerConsultaConfig(String clavePantalla, String nombreConsulta) {
        PantallaConfig pantallaConfig = obtenerConfiguracion(clavePantalla);
        return pantallaConfig.obtenerConsultaPorNombre(nombreConsulta);
    }

    public Map<String, PantallaConfig> obtenerTodasConfiguraciones() {
        return new HashMap<>(configuraciones);
    }
}
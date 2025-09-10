package com.cfo.reporting.service;

import com.cfo.reporting.service.config.PantallaService;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String query= "select * from tbl_cfo_savlst";
        System.out.println("QUery modificado "+query.replaceAll("(?i)select\\s+\\*","select count(*) "));
    }
    public static void manolo(String[] args) {
        // Configurar DataSource y NamedParameterJdbcTemplate
        DataSource dataSource = crearDataSource();
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        // Crear servicio
        PantallaService pantallaService = new PantallaService(jdbcTemplate);

        try {
            String clavePantalla = "scr_worksheet";
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("glPeriodPrev", "202502");
            parametros.put("glPeriod", "202503");

            Map<String, Map<String, Object>> resultados =
                    pantallaService.obtenerTodasConsultasPantalla(clavePantalla,parametros);

            System.out.println("\nTodas las consultas de la pantalla '" + clavePantalla + "':");
            for (Map.Entry<String, Map<String, Object>> entry : resultados.entrySet()) {
                System.out.println("Consulta: " + entry.getKey());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Ejemplo 2: Obtener datos de una consulta específica con parámetros
//        try {
//            String clavePantalla = "scr_worksheet";
//            String nombreConsulta = "gldays";
//
//            Map<String, Object> parametros = new HashMap<>();
//            parametros.put("glPeriod", "202502");
//
//
//            Object datos = pantallaService.obtenerDatosConsulta(clavePantalla, nombreConsulta, parametros);
//
//            System.out.println("\nDatos para consulta " + nombreConsulta + ":");
//            System.out.println(datos);
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }

    }

    private static DataSource crearDataSource() {
        // Configurar según tu base de datos
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/cfo_reporting");
        dataSource.setUsername("root");
        //dataSource.setPassword("contraseña");
        return dataSource;
    }
}
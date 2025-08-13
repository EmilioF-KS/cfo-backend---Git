package com.cfo.reporting.config;

import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {
    @Bean
    public JpaProperties jpaProperties() {
        JpaProperties props = new JpaProperties();
        props.getProperties().put("javax.persistence.schema-generation.relationship", "none");
        return props;
    }
}
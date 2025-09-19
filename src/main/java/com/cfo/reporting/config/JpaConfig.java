package com.cfo.reporting.config;

import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.cfo.reporting.repository",
        entityManagerFactoryRef = "entityManagerFactory"
)
public class JpaConfig {

    @Bean
    public JpaProperties jpaProperties() {
        JpaProperties props = new JpaProperties();
        props.getProperties().put("javax.persistence.schema-generation.relationship", "none");
        return props;
    }

    @Bean
    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("org.hibernate.hbm2ddl.auto","update");
        properties.setProperty("hibernate.dialect","org.hibernate.dialect.MySQL5Dialect");
        return properties;
    }

}
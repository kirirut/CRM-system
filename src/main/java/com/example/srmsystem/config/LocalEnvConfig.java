package com.example.srmsystem.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
@Profile("local")
public class LocalEnvConfig {


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/CRM");
        dataSource.setUsername("kirirut");
        dataSource.setPassword("2811");
        return dataSource;
    }
}

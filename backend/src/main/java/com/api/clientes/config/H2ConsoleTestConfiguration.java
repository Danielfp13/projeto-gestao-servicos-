package com.api.clientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;

@Configuration
public class H2ConsoleTestConfiguration {

    @Bean
    @Primary
    public H2ConsoleProperties h2ConsoleProperties() {
        H2ConsoleProperties properties = new H2ConsoleProperties();
        properties.setEnabled(true);
        properties.setPath("/h2-console");
        return properties;
    }
}

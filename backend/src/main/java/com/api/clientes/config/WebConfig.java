package com.api.clientes.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Definindo as origens permitidas
        corsConfiguration.setAllowedOrigins(List.of(
                "https://gestao-servicos.netlify.app",
                "http://localhost:4200"
        ));

        // Definindo os cabeçalhos permitidos
        corsConfiguration.setAllowedHeaders(List.of(
                "Origin", "Content-Type", "Accept", "Authorization"
        ));

        // Definindo os métodos permitidos
        corsConfiguration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Definindo se permite credenciais
        corsConfiguration.setAllowCredentials(true);

        // Expondo cabeçalhos necessários
        corsConfiguration.addExposedHeader("Authorization");
        corsConfiguration.addExposedHeader("Access-Control-Allow-Origin");

        // Registrando as configurações CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        // Criando e retornando o filtro com prioridade alta
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<>(corsFilter);
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filter;
    }
}

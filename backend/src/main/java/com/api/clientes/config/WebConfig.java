package com.api.clientes.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Adicionar os domínios permitidos
        corsConfiguration.setAllowedOrigins(List.of(
                "https://gestao-servicos.netlify.app",  // Frontend no Netlify
                "http://localhost:4200"  // Desenvolvimento local
        ));

        // Permitir todos os cabeçalhos
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        // Permitir todos os métodos HTTP (POST, GET, PUT, DELETE, etc.)
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));

        // Permitir credenciais (cookies, tokens, etc.)
        corsConfiguration.setAllowCredentials(true);

        // Adicionar tempo de validade da configuração (opcional)
        corsConfiguration.setMaxAge(3600L);

        // Definir a configuração para todas as rotas do backend
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        // Registrar o filtro de CORS
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Alta prioridade
        return bean;
    }
}

package com.api.clientes.config;

import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("test")
@AllArgsConstructor
public class LocalConfig {

    private BCryptPasswordEncoder encoder;
    private UsuarioRepository repository;

    @Bean
    public void startBdTest() {
        Usuario usuario = new Usuario(1, "ana@email.com", "Ana Maria", encoder.encode("123"));
        Usuario usuario2 = new Usuario(2, "maria@email.com", "Maria", encoder.encode("123"));
        usuario2.addPerfil(Perfil.ADMIN);
        repository.save(usuario);
        repository.save(usuario2);
    }
}

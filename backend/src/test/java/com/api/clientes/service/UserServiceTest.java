package com.api.clientes.service;

import com.api.clientes.Service.UserService;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.repository.UsuarioRepository;
import com.api.clientes.security.UserSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UserService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        start();
    }

    @Test
    @DisplayName("Deve buscar um usuário por nome")
    void loadUserByUsernameTest() {
        String username = "user@email.com.br";
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(usuario));

        UserSS userSS = (UserSS) service.loadUserByUsername(username);

        assertThat(userSS.getId()).isEqualTo(usuario.getId());
        assertThat(userSS.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(userSS.getPassword()).isEqualTo(usuario.getPassword());
        assertThat(userSS.getAuthorities()).isEqualTo(usuario.getPerfis()
                .stream().map(x -> new SimpleGrantedAuthority( x.getDescricao()))
                .collect(Collectors.toList())  );
        verify(repository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário por nome e não encontrar encontrado")
    void loadUserByUsernameNotFoundTest() {
        String username = "user@email.com";
        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception =  assertThrows(ResponseStatusException.class,
                () -> service.loadUserByUsername(username));
        assertThat("Login não encontrado.").isEqualTo(exception.getReason());
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(exception.getStatusCode());

        verify(repository, times(1)).findByUsername(username);
    }

    void start() {
        usuario = new Usuario(1, "user1", "password", "123");
    }
}

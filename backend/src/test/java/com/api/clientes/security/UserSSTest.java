package com.api.clientes.security;

import com.api.clientes.model.enums.Perfil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserSSTest {

    @Test
    @DisplayName("Deve retornar as autoridades corretas")
    public void testGetAuthorities() {
        // Criar um conjunto de perfis
        Set<Perfil> perfis = new HashSet<>(Arrays.asList(Perfil.ADMIN, Perfil.USER));

        UserSS user = new UserSS(1, "username", "password", perfis);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertTrue(authorities.contains(new SimpleGrantedAuthority(Perfil.ADMIN.getDescricao())));
        assertTrue(authorities.contains(new SimpleGrantedAuthority(Perfil.USER.getDescricao())));
    }

    @Test
    @DisplayName("Deve retornar true se o usuário possui o perfil especificado")
    public void testHasRole() {
        // Criar um conjunto de perfis
        Set<Perfil> perfis = new HashSet<>(Collections.singletonList(Perfil.USER));

        // Criar uma instância de UserSS
        UserSS user = new UserSS(1, "username", "password", perfis);

        // Verificar se o usuário possui o perfil de USUARIO
        assertTrue(user.hasRole(Perfil.USER));

        // Verificar se o usuário não possui o perfil de ADMIN
        assertFalse(user.hasRole(Perfil.ADMIN));
    }

    @Test
    @DisplayName("Deve retornar a senha correta")
    public void testGetPassword() {
        String password = "password";
        UserSS user = new UserSS(1, "username", password, Collections.emptySet());
        assertThat(password).isEqualTo(user.getPassword());
    }

    @Test
    @DisplayName("Deve retornar o nome de usuário correto")
    public void testGetUsername() {
        String username = "username";
        UserSS user = new UserSS(1, username, "password", Collections.emptySet());
        assertThat(username).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Deve retornar true para conta não expirada")
    public void testIsAccountNonExpired() {
        UserSS user = new UserSS(1, "username", "password", Collections.emptySet());
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    @DisplayName("Deve retornar true para conta não bloqueada")
    public void testIsAccountNonLocked() {
        UserSS user = new UserSS(1, "username", "password", Collections.emptySet());
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    @DisplayName("Deve retornar true para credenciais não expiradas")
    public void testIsCredentialsNonExpired() {
        UserSS user = new UserSS(1, "username", "password", Collections.emptySet());
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Deve retornar true para usuário habilitado")
    public void testIsEnabled() {
        UserSS user = new UserSS(1, "username", "password", Collections.emptySet());
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar o ID correto")
    public void testGetId() {
        Integer id = 1;
        UserSS user = new UserSS(id, "username", "password", Collections.emptySet());
        assertThat(id).isEqualTo(user.getId());
    }
}



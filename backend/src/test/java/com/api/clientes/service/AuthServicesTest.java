package com.api.clientes.service;

import com.api.clientes.Service.AuthServices;
import com.api.clientes.dto.security.AccountCredentialsDTO;
import com.api.clientes.dto.security.TokenDTO;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.repository.UsuarioRepository;
import com.api.clientes.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServicesTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AuthServices authServices;

    private Usuario usuario;
    private AccountCredentialsDTO accountCredentialsDTO;
    private TokenDTO tokenDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        start();
    }

    @Test
    @DisplayName("Deve gerar um token de acesso válido ao fazer o signin com parâmetros válidos")
    void signinTest() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(repository.findByUsername(eq(accountCredentialsDTO.getUsername()))).thenReturn(Optional.of(usuario));
        when(tokenProvider.createAccessToken(eq(accountCredentialsDTO.getUsername()), eq(usuario.getNome()), any())).thenReturn(tokenDTO);

        TokenDTO result = authServices.signin(accountCredentialsDTO);
        assertThat(result).isNotNull();
        assertThat(accountCredentialsDTO.getUsername()).isEqualTo(result.getUserName());
        assertThat(result.getAuthenticate()).isTrue();
        assertThat(tokenDTO.getCreate()).isEqualTo(result.getCreate());
        assertThat(tokenDTO.getExpiration()).isEqualTo(result.getExpiration());
        assertThat(tokenDTO.getAccesToken()).isEqualTo(result.getAccesToken());
        assertThat(tokenDTO.getRefreshToken()).isEqualTo(result.getRefreshToken());
    }

    @Test
    @DisplayName("Deve lançar InvalidRequestException ao fazer o signin com parâmetros inválidos")
    void signinWithInvalidParamsTest() {

        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO(null, "password");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.signin(accountCredentialsDTO));
        assertThat("Requisisão inválida, dados incompletos.").isEqualTo(exception.getReason());
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException com status 401 e mensagem adequada ao fazer o signin com credenciais inválidas")
    void signinWithInvalidCredentialsTest() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.signin(accountCredentialsDTO));

        assertEquals("Credenciais de usuário/senha inválidas!", exception.getReason());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
    @Test
    @DisplayName("Deve lançar ResponseStatusException com status 500 e mensagem adequada ao ocorrer erro interno do servidor no refreshToken")
    void refreshTokenInternalErrorTest() {
        // Arrange
        String email = "julia@email.com";
        String refreshToken = "refreshToken";
        when(repository.findByUsername(email)).thenReturn(Optional.of(new Usuario()));
        when(tokenProvider.refreshToken(refreshToken)).thenReturn(null); // Simulando erro interno do servidor

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.refreshToken(email, refreshToken));

        assertEquals("Erro interno do servidor!", exception.getReason());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve gerar um novo token válido ao fazer o refresh do token com parâmetros válidos")
    void refreshTokenTest() {

        String email = "ana@email.com";
        String refreshToken = "refresh_token";

        when(repository.findByUsername(anyString())).thenReturn(Optional.of(usuario));
        when(tokenProvider.refreshToken(anyString())).thenReturn(tokenDTO);

        TokenDTO result = authServices.refreshToken(email, refreshToken);

        assertThat(result).isNotNull();
        assertThat(email).isEqualTo(result.getUserName());
        assertThat(result.getAuthenticate()).isTrue();
        assertThat(tokenDTO.getCreate()).isEqualTo(result.getCreate());
        assertThat(tokenDTO.getExpiration()).isEqualTo(result.getExpiration());
        assertThat(tokenDTO.getAccesToken()).isEqualTo(result.getAccesToken());
        assertThat(tokenDTO.getRefreshToken()).isEqualTo(result.getRefreshToken());
    }

    @Test
    @DisplayName("Deve lançar InvalidRequestException ao fazer o refresh do token com parâmetros inválidos")
    void refreshTokenWithInvalidParamsTest() {
        String email = null;
        String refreshToken = "refresh_token";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.refreshToken(email, refreshToken));
        assertThat("Requisisão inválida, dados incompletos.").isEqualTo(exception.getReason());
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException ao fazer o refresh do token com email inválido")
    void refreshTokenWithInvalidEmailTest() {

        String email = "ana@email.com.br.com";
        String refreshToken = "refresh_token";

        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.refreshToken(email, refreshToken));

        assertThat("Esse email  ana@email.com.br.com não existe!").isEqualTo(exception.getReason());
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException com status 404 e mensagem adequada ao fazer o signin com email não encontrado")
    void signinWithInvalidEmailTest() {
        // Arrange
        String email = "julia@email.com";
        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO(email, "password");
        when(repository.findByUsername(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.signin(accountCredentialsDTO));
        assertEquals("Esse email " + email + " não existe!", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException com status 500 e mensagem adequada quando tokenDTO for nulo")
    void signinWithNullTokenDTOTest() {
        // Arrange
        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO("email", "password");
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(new Usuario()));
        when(tokenProvider.createAccessToken(anyString(), anyString(), anyList())).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.signin(accountCredentialsDTO));
        assertEquals("Erro interno do servidor!", exception.getReason());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }





/*    @Test
    @DisplayName("Deve lançar ResponseStatusException com status 500 e mensagem adequada ao fazer o signin com erro interno do servidor")
    void signinWithInternalServerErrorTest() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        when(repository.findByUsername(eq(accountCredentialsDTO.getUsername()))).thenReturn(Optional.of(usuario));
        doThrow(new RuntimeException("Erro interno do servidor!")).when(tokenProvider).createAccessToken(anyString(), anyString(), any());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authServices.signin(accountCredentialsDTO));
        assertEquals("Erro interno do servidor!", exception.getReason());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }*/




    /*    assertEquals(exception.getReason(),"Erro interno do servidor!");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        */


    private void start() {
        usuario = new Usuario(1, "ana@email.com", "Ana Maria", "123");
        accountCredentialsDTO = new AccountCredentialsDTO("ana@email.com", "123");
        tokenDTO = new TokenDTO(accountCredentialsDTO.getUsername(), true, new Date(),
                new Date(System.currentTimeMillis() + 3600000), "access_token", "refresh_token");

    }
}

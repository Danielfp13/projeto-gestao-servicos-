package com.api.clientes.service;

import com.api.clientes.Service.EmailService;
import com.api.clientes.Service.PasswordResetTokenService;
import com.api.clientes.Service.UsuarioService;
import com.api.clientes.model.entity.PasswordResetToken;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.repository.PasswordResetTokenRepository;
import com.api.clientes.security.jwt.JwtTokenProvider;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository repository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

    private Usuario usuario;
    private PasswordResetToken resetToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario(1, "maria@email.com", "Maria", "123");
        resetToken = new PasswordResetToken(1, "test-token", usuario, LocalDateTime.now().plusHours(1L));
    }

    @Test
    @DisplayName("Deve enviar um email de redefinição de senha")
    void forgotPassword() throws MessagingException {
        Map<String, String> request = new HashMap<>();
        request.put("email", "ana@email.com");
        when(usuarioService.findByUsername(anyString())).thenReturn(usuario);
        when(jwtTokenProvider.generatePasswordResetToken(usuario)).thenReturn(resetToken.getToken());
        when(repository.save(any(PasswordResetToken.class))).thenReturn(resetToken);
        passwordResetTokenService.forgotPassword(request);
        verify(emailService, times(1)).enviarEmailRedefinicaoSenha(usuario, resetToken.getToken());
    }



    @Test
    @DisplayName("Deve criar um token para redefinição de senha")
    void createToken() {
        when(jwtTokenProvider.generatePasswordResetToken(usuario)).thenReturn(resetToken.getToken());
        when(repository.save(any(PasswordResetToken.class))).thenReturn(resetToken);

        PasswordResetToken result = passwordResetTokenService.createToken(usuario);

        assertNotNull(result);
        assertEquals(resetToken.getToken(), result.getToken());
        assertEquals(usuario, result.getUsuario());
    }

    @Test
    @DisplayName("Deve alterar a senha do usuário se o token for válido e não tiver expirado")
    void resetPassword() {

        String token = "test-token";
        String password = "newpassword";
        Map<String, String> request = new HashMap<>();
        request.put("token", token);
        request.put("password", password);

        when(repository.findByToken(anyString())).thenReturn(Optional.of(resetToken));
        doNothing().when(usuarioService).updatePassword(any(), anyString());
        doNothing().when(repository).delete(resetToken);

        passwordResetTokenService.resetPassword(request);

        verify(usuarioService, times(1)).updatePassword(usuario, password);
        verify(repository, times(1)).delete(resetToken);
    }

    @Test
    @DisplayName("Deve buscar um token de redefinição de senha")
    void findByToken() {

        String token = "test-token";
        when(repository.findByToken(token)).thenReturn(Optional.of(resetToken));

        PasswordResetToken result = passwordResetTokenService.findByToken(token);
        assertNotNull(result);
        assertEquals(resetToken, result);
    }

    @Test
    @DisplayName("Deve apagar um token de redefinição de senha")
    void delete() {
        doNothing().when(repository).delete(any());
        passwordResetTokenService.delete(resetToken);
        passwordResetTokenService.delete(resetToken);
        verify(repository, times(2)).delete(resetToken);
    }

    @Test
    @DisplayName("Deve apagar os tokens de redefinição de senha expirados todos os dias às 18:00")
    void apagarTokensVencidos() {

        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken expiredToken1 = new PasswordResetToken();
        expiredToken1.setExpiryDate(now.minusDays(1));
        PasswordResetToken expiredToken2 = new PasswordResetToken();
        expiredToken2.setExpiryDate(now.minusHours(1));
        List<PasswordResetToken> expiredTokens = Arrays.asList(expiredToken1, expiredToken2);
        when(repository.findByDataExpiracaoBefore(any())).thenReturn(expiredTokens);
        passwordResetTokenService.apagarTokensVencidos();
        verify(repository, times(1)).deleteAll(expiredTokens);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar redefinir a senha sem fornecer um token")
    void resetPassword_MissingToken() {
        String password = "newpassword";
        Map<String, String> request = new HashMap<>();
        request.put("password", password);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            passwordResetTokenService.resetPassword(request);
        });
        assertEquals("O token de redefinição de senha não foi informado.", exception.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar redefinir a senha sem fornecer uma nova senha")
    void resetPassword_MissingPassword() {
        String token = "valid-token";
        Map<String, String> request = new HashMap<>();
        request.put("token", token);

        ResponseStatusException exception =  assertThrows(ResponseStatusException.class, () -> {
            passwordResetTokenService.resetPassword(request);
        });

        assertEquals("A nova senha não foi informada.", exception.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar redefinir a senha de um usuário com token inexistente")
    void resetPassword_NonexistentUser() {
        String token = "token";
        String password = "newpassword";
        Map<String, String> request = new HashMap<>();
        request.put("token", token);
        request.put("password", password);

        ResponseStatusException exception =  assertThrows(ResponseStatusException.class, () -> {
            passwordResetTokenService.resetPassword(request);
        });

        assertEquals("O token informado não existe.", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(repository, times(1)).findByToken(token);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar redefinir a senha com um token expirado")
    void resetPassword_tokenExpired() {
        String token = "token";
        String password = "newpassword";
        Map<String, String> request = new HashMap<>();
        request.put("token", token);
        request.put("password", password);
        resetToken.setExpiryDate(LocalDateTime.now().minusDays(1L));

        when(repository.findByToken(anyString())).thenReturn(Optional.of(resetToken));
        doNothing().when(repository).delete(resetToken);

        ResponseStatusException exception =  assertThrows(ResponseStatusException.class, () -> {
            passwordResetTokenService.resetPassword(request);
        });

        assertEquals("O token está vencido.", exception.getReason());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(repository, times(1)).findByToken(token);
    }

}


package com.api.clientes.service;

import com.api.clientes.Service.EmailService;

import com.api.clientes.model.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    private Usuario usuario;
    private String token;

    @BeforeEach
    public void setup() {
        start();
    }

    @Test
    @DisplayName("Deve enviar um email de redefinição de senha")
    public void EnviarEmailRedefinicaoSenhaTest() {
        String urlFront = "http://localhost:4200";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        emailService.enviarEmailRedefinicaoSenha(usuario, token, urlFront);

        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve Retornar uma erro ao tentar enviar um email")
    public void EnviarEmailRedefinicaoSenhaExceptionTest() {
        MailException mailException = new MailException("Failed to send email") {};
        doThrow(mailException).when(emailSender).send(any(SimpleMailMessage.class));
        String urlFront = "http://localhost:4200";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emailService.enviarEmailRedefinicaoSenha(usuario, token, urlFront);
        });

        assertThat(exception.getReason()).isEqualTo("Erro ao enviar email.");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void start() {
        usuario = new Usuario(1, "julia@email.com", "Julia", "123");
        token = "token";
    }
}


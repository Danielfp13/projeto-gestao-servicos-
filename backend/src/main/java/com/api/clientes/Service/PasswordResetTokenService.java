package com.api.clientes.Service;

import com.api.clientes.dto.ForgotPasswordRequestDTO;
import com.api.clientes.dto.ResetPasswordRequestDTO;
import com.api.clientes.model.entity.PasswordResetToken;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.repository.PasswordResetTokenRepository;
import com.api.clientes.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    private PasswordResetTokenRepository repository;
    private UsuarioService usuarioService;
    private EmailService emailService;
    private JwtTokenProvider jwtTokenProvider;

    public void forgotPassword(ForgotPasswordRequestDTO request) {
        Usuario usuario = usuarioService.findByUsername(request.getEmail());
        PasswordResetToken token = this.createToken(usuario);
        emailService.enviarEmailRedefinicaoSenha(usuario, token.getToken(), request.getUrlFront()); // enviar e-mail com link para redefinição de senha
    }

    public PasswordResetToken createToken(Usuario usuario) {
        String token = jwtTokenProvider.generatePasswordResetToken(usuario);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUsuario(usuario);
        resetToken.setToken(token);
        //resetToken.setExpiryDate(LocalDateTime.now().plusHours(1L));
        // Obtém a data e hora atual no fuso horário de São Paulo
        ZoneId fusoHorarioDeSaoPaulo = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime now = ZonedDateTime.now(fusoHorarioDeSaoPaulo);
        ZonedDateTime expiryDateTime = now.plusHours(1L);

        // Converte para LocalDateTime (sem informações de fuso horário)
        LocalDateTime expiryLocalDateTime = expiryDateTime.toLocalDateTime();

        resetToken.setExpiryDate(expiryLocalDateTime);
        return repository.save(resetToken);
    }

    public void resetPassword(ResetPasswordRequestDTO request) {
        String token = request.getToken();
        String password = request.getPassword();
        if (password == "" || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha não foi informada.");
        }
        if (token == "" || token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O token de redefinição de senha não foi informado.");
        }

        PasswordResetToken resetToken = this.findByToken(token);

        ZoneId fusoHorarioDeSaoPaulo = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime now = ZonedDateTime.now(fusoHorarioDeSaoPaulo);
        LocalDateTime currentDateTime = now.toLocalDateTime();
        if (resetToken.getExpiryDate().isBefore(currentDateTime)) {
            this.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O token está vencido.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuarioService.updatePassword(usuario, password);
        this.delete(resetToken);
    }

    public PasswordResetToken findByToken(String token) {
        return repository.findByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O token informado não existe."));
    }

    public void delete(PasswordResetToken token) {
        repository.delete(token);
    }

    @Scheduled(cron = "0 00 18 * * ?") // Executa todos os dias às 18:00
    public void apagarTokensVencidos() {
        List<PasswordResetToken> tokensVencidos = repository.findByDataExpiracaoBefore(LocalDateTime.now());
        repository.deleteAll(tokensVencidos);
    }
}

package com.api.clientes.Service;

import com.api.clientes.model.entity.PasswordResetToken;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.repository.PasswordResetTokenRepository;
import com.api.clientes.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    private PasswordResetTokenRepository repository;
    private UsuarioService usuarioService;
    private EmailService emailService;
    private JwtTokenProvider jwtTokenProvider;

    public void forgotPassword(Map<String, String> request) {
        String email = request.get("email");
        Usuario usuario = usuarioService.findByUsername(email);
        PasswordResetToken token = this.createToken(usuario);
        emailService.enviarEmailRedefinicaoSenha(usuario, token.getToken()); // enviar e-mail com link para redefinição de senha
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

    public void resetPassword(Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");
        if (password == "" || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha não foi informada.");
        }
        if (token == "" || token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O token de redefinição de senha não foi informado.");
        }

        PasswordResetToken resetToken = this.findByToken(token);

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
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

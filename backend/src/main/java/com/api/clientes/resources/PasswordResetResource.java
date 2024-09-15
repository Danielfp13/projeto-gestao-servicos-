package com.api.clientes.resources;

import com.api.clientes.Service.PasswordResetTokenService;
import com.api.clientes.Service.UsuarioService;
import com.api.clientes.dto.ForgotPasswordRequestDTO;
import com.api.clientes.dto.ResetPasswordRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/password")
@Tag(name = "Password Endpoint", description = "Operações relacionadas a recuperar senha.")
public class PasswordResetResource {
    private PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/forgot")
    @Operation(summary = "Esqueceu a senha", description = "O usuário previamente cadastrado deve informar seu e-mail, e o sistema enviará a URL da " +
            "página de recuperação de senha. O sistema enviará um e-mail contendo um link com o token para redefinição de senha.")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetTokenService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    @Operation(summary = "Redefinir senha", description = "Permite  redefinir sua senha usando um token de redefinição recebido por e-mail.")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordResetTokenService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}


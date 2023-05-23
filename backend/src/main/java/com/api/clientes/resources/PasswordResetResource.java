package com.api.clientes.resources;

import com.api.clientes.Service.PasswordResetTokenService;
import com.api.clientes.Service.UsuarioService;
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
public class PasswordResetResource {
    private PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        passwordResetTokenService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        passwordResetTokenService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}


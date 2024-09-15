package com.api.clientes.resources;

import com.api.clientes.Service.AuthServices;
import com.api.clientes.dto.security.AccountCredentialsDTO;
import com.api.clientes.dto.security.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Endpoint", description = "Operações relacionadas ao login.")
public class AuthController {

    @Autowired
    AuthServices authServices;

    @PostMapping(value = "/signin")
    @Operation(summary = "Login de usuário", description = "O usuário previamente cadastrado deve informar seu username e password para logar no sistema.")
    public ResponseEntity signin(@RequestBody AccountCredentialsDTO data) {
        TokenDTO token = authServices.signin(data);
        return ResponseEntity.ok(token);
    }

    @PutMapping(value = "/refresh/{username}")
    @Operation(summary = "Atualização de token", description = "O usuário previamente cadastrado deve informar seu username para receber um novo token.")

    public ResponseEntity refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken) {
        TokenDTO token = authServices.refreshToken(username, refreshToken);
        return ResponseEntity.ok(token);
    }
}

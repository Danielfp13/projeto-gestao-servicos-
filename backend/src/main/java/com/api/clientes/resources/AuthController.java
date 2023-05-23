package com.api.clientes.resources;

import com.api.clientes.Service.AuthServices;
import com.api.clientes.dto.security.AccountCredentialsDTO;
import com.api.clientes.dto.security.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthServices authServices;

    @PostMapping(value = "/signin")
    public ResponseEntity signin(@RequestBody AccountCredentialsDTO data) {
        TokenDTO token = authServices.signin(data);
        return ResponseEntity.ok(token);
    }

    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken) {
        TokenDTO token = authServices.refreshToken(username, refreshToken);
        return ResponseEntity.ok(token);
    }
}

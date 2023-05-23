package com.api.clientes.Service;

import com.api.clientes.dto.security.AccountCredentialsDTO;
import com.api.clientes.dto.security.TokenDTO;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.repository.UsuarioRepository;
import com.api.clientes.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
public class AuthServices {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioRepository repository;

    public TokenDTO signin(AccountCredentialsDTO data) {
        if (checkIfParamsIsNotNull(data))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisisão inválida, dados incompletos.");
        try {
            String email = data.getUsername();
            String password = data.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            Usuario usuario = repository.findByUsername(email).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Esse email " + email + " não existe!"));
            TokenDTO tokenDTO = tokenProvider.createAccessToken(email, usuario.getNome(), usuario.getPerfis()
                    .stream().map(x -> Perfil.toEnum(x.getCod()).getDescricao()).collect(Collectors.toList())
            );
            if (tokenDTO != null) {
                return tokenDTO;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor!");
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais de usuário/senha inválidas!");
        }
    }

    public TokenDTO refreshToken(String email, String refreshToken) {
        if (checkIfParamsIsNotNull(email, refreshToken))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisisão inválida, dados incompletos.");
        Usuario usuario = repository.findByUsername(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Esse email  " + email + " não existe!"));
        TokenDTO tokenDTO = tokenProvider.refreshToken(refreshToken);
        if (tokenDTO != null) {
            return tokenDTO;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor!");
        }
    }

    public boolean checkIfParamsIsNotNull(String username, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() ||
                username == null || username.isBlank();
    }

    public boolean checkIfParamsIsNotNull(AccountCredentialsDTO data) {
        return data == null || data.getUsername() == null || data.getUsername().isBlank()
                || data.getPassword() == null || data.getPassword().isBlank();
    }
}


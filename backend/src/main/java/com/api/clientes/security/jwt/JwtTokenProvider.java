package com.api.clientes.security.jwt;

import com.api.clientes.dto.security.TokenDTO;
import com.api.clientes.model.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenDTO createAccessToken(String username, String nome, List<String> perfils) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        var accessToken = getAccessToken(username, nome, perfils, now, validity);
        var refreshToken = getRefreshToken(username, nome, perfils, now);

        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }


    public TokenDTO refreshToken(String refreshToken) {
        if (refreshToken.contains("Bearer ")) refreshToken =
                refreshToken.substring("Bearer ".length());

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String username = decodedJWT.getSubject();
        String nome = decodedJWT.getClaim("usuario").asString();
        System.out.println(nome);
        List<String> perfils = decodedJWT.getClaim("perfils").asList(String.class);
        return createAccessToken(username, nome, perfils);
    }

    private String getAccessToken(String username, String nome, List<String> perfils, Date now, Date validity) {
        String issuerUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withClaim("perfils", perfils)
                .withClaim("usuario", nome)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm)
                .strip();
    }

    private String getRefreshToken(String username, String nome, List<String> perfils, Date now) {
        String issuerUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath().build().toUriString();
        Date validityRefreshToken = new Date(now.getTime() + (validityInMilliseconds * 5));
        return JWT.create()
                .withClaim("perfils", perfils)
                .withClaim("usuario", nome)
                .withIssuedAt(now)
                .withExpiresAt(validityRefreshToken)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm)
                .strip();
    }

    public String generatePasswordResetToken(Usuario usuario) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return JWT.create()
                .withSubject(usuario.getUsername())
                .withSubject(usuario.getNome())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withIssuer(issuerUrl)
                .sign(algorithm);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService
                .loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            if (decodedJWT.getExpiresAt().before(new Date())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O token informado é inválido ou esta expirado!");
        }
    }
}

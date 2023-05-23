package com.api.clientes.security.jwt;

import com.api.clientes.Service.PasswordResetTokenService;
import com.api.clientes.Service.UserService;
import com.api.clientes.model.entity.PasswordResetToken;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;

public class JwtTokenFilter extends GenericFilterBean {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    UserService userDetailsServiceImpl;

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    public JwtTokenFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        String token = tokenProvider.resolveToken((HttpServletRequest) request);
        HttpServletResponse res = null;
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            res = (HttpServletResponse) response;
            if (token != null && tokenProvider.validateToken(token)) {
                Authentication auth = tokenProvider.getAuthentication(token);
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            if (((HttpServletRequest) request).getRequestURI().contains("/api/reset-password")) {
                token = request.getParameter("token");
                PasswordResetToken resetToken = passwordResetTokenService.findByToken(token);

                if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(resetToken.getUsuario().getUsername());

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            chain.doFilter(request, response);
        } catch (TokenExpiredException ex) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"message\": \"Token expirado\"}");
        }/* catch (TokenInvalidException ex) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write("{\"message\": \"Token inválido\"}");
        }*/
    }
}
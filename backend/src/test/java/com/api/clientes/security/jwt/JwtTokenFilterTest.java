package com.api.clientes.security.jwt;

import com.api.clientes.Service.PasswordResetTokenService;
import com.api.clientes.Service.UserService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class JwtTokenFilterTest {
    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    String token;
    String username;
    HttpServletRequest request;
    HttpServletResponse response;
    FilterChain chain;
    SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.start();
    }

    @Test
    @DisplayName("Deve filtrar o token JWT e autenticar o usuário")
    public void doFilter_withValidToken_shouldAuthenticateUser() throws ServletException, IOException {

        when(tokenProvider.resolveToken(request)).thenReturn(token);
        when(tokenProvider.validateToken(token)).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername(eq(username))).thenReturn(userDetails);

        Authentication authentication = mock(Authentication.class);
        when(tokenProvider.getAuthentication(token)).thenReturn(authentication);

        // Mock getRequestURI() to return a valid URI
        when(request.getRequestURI()).thenReturn("/api/some-path");

        // Act
        jwtTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(tokenProvider, times(1)).resolveToken(request);
        verify(tokenProvider, times(1)).validateToken(token);
        verify(tokenProvider, times(1)).getAuthentication(token);
        verify(userService, times(0)).loadUserByUsername(eq(username));
        verify(securityContext, times(1)).setAuthentication(authentication);
        verify(chain, times(1)).doFilter(request, response);
    }



    @Test
    @DisplayName("Deve passar pelo filtro quando não houver token presente")
    public void doFilter_withoutToken_shouldPassThroughFilter() throws ServletException, IOException {
        // Arrange
        when(tokenProvider.resolveToken(request)).thenReturn(null);

        // Act
        jwtTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
    }

    private void start() {
        token = "valid_token";
        username = "test_user";
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }
}


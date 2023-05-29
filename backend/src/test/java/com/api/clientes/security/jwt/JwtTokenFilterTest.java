package com.api.clientes.security.jwt;

import com.api.clientes.Service.PasswordResetTokenService;
import com.api.clientes.Service.UserService;
import com.api.clientes.model.entity.PasswordResetToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve filtrar o token JWT e autenticar o usuário")
    public void doFilter_withValidToken_shouldAuthenticateUser() throws ServletException, IOException {
        // Arrange
        String token = "valid_token";
        String username = "test_user";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

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


/*    @Test
    @DisplayName("Filtro deve autenticar o usuário com solicitação de redefinição de senha")
    public void doFilter_withResetPasswordRequest_shouldAuthenticateUser() throws ServletException, IOException {
        // Arrange
        String token = "reset_password_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(tokenProvider.resolveToken(request)).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/reset-password");
        when(request.getParameter("token")).thenReturn(token);

        PasswordResetToken resetToken = mock(PasswordResetToken.class);
        when(passwordResetTokenService.findByToken(anyString())).thenReturn(resetToken);
        when(resetToken.getExpiryDate()).thenReturn(LocalDateTime.now().plusDays(1));

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);

        Authentication authentication = mock(Authentication.class);
        when(tokenProvider.getAuthentication(anyString())).thenReturn(authentication);

        // Act
        jwtTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(tokenProvider, times(1)).resolveToken(request);
        verify(tokenProvider, never()).validateToken(anyString());
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getParameter("token");
        verify(passwordResetTokenService, times(1)).findByToken(token);
        verify(userService, times(1)).loadUserByUsername(anyString());
        verify(securityContext, times(1)).setAuthentication(authentication);
        verify(chain, times(1)).doFilter(request, response);
    }*/




/*

    @Test
    public void doFilter_withExpiredToken_shouldReturnUnauthorizedStatus() throws ServletException, IOException {
        // Arrange
        String token = "expired_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(tokenProvider.resolveToken(request)).thenReturn(token);
        when(tokenProvider.validateToken(token)).thenReturn(false);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        // Act
        jwtTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(tokenProvider, times(1)).resolveToken(request);
        verify(tokenProvider, times(1)).validateToken(token);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(response.getWriter(), times(1)).write("{\"message\": \"Token expirado\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void doFilter_withResetPasswordRequestAndExpiredToken_shouldNotAuthenticateUser() throws ServletException, IOException {
        // Arrange
        String token = "expired_reset_password_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(tokenProvider.resolveToken(request)).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/reset-password");
        when(request.getParameter("token")).thenReturn(token);

        PasswordResetToken resetToken = mock(PasswordResetToken.class);
        when(passwordResetTokenService.findByToken(token)).thenReturn(resetToken);
        when(resetToken.getExpiryDate()).thenReturn(LocalDateTime.now().minusDays(1));

        // Act
        jwtTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(tokenProvider, times(1)).resolveToken(request);
        verify(tokenProvider, never()).validateToken(anyString());
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getParameter("token");
        verify(passwordResetTokenService, times(1)).findByToken(token);
        verify(userService, never()).loadUserByUsername(anyString());
        verify(SecurityContextHolder.getContext(), never()).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(chain, times(1)).doFilter(request, response);
    }*/
}


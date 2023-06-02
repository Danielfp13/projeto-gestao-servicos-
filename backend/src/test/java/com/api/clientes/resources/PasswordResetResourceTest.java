package com.api.clientes.resources;

import com.api.clientes.Service.PasswordResetTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static com.api.clientes.resources.util.JsonConvertionUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetResourceTest {

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    MockMvc mockMvc;

    @InjectMocks
    private PasswordResetResource passwordResetResource;

    private static final String PASSWORD_API_URI_PATH = "/api/password";

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve enviar um email de recuperação de senha ao usuário")
    void forgotPassword() throws Exception {
        Map<String, String> request = new HashMap<>();
        doNothing().when(passwordResetTokenService).forgotPassword(any());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PASSWORD_API_URI_PATH.concat("/forgot"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk());
        verify(passwordResetTokenService, times(1)).forgotPassword(request);
    }


    @Test
    @DisplayName("Deve altera a senha do usuário")
    void resetPassword() throws Exception {
        Map<String, String> request = new HashMap<>();

        doNothing().when(passwordResetTokenService).resetPassword(any());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PASSWORD_API_URI_PATH.concat("/reset"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk());
        verify(passwordResetTokenService, times(1)).resetPassword(request);
    }
}
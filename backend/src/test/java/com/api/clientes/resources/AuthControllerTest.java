package com.api.clientes.resources;

import com.api.clientes.Service.AuthServices;
import com.api.clientes.dto.security.AccountCredentialsDTO;
import com.api.clientes.dto.security.TokenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static com.api.clientes.resources.util.JsonConvertionUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    @Mock
    private AuthServices authServices;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AuthController authController;

    private TokenDTO tokenDTO;

    private static final String AUTH_API_URI_PATH = "/auth";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        startAuth();
    }


    @Test
    @DisplayName("Deve retornar status 200 e o token ao fazer o signin com parâmetros válidos")
    void signinTest() throws Exception {

        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO("ana@email.com", "123");

        when(authServices.signin(any())).thenReturn(tokenDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(AUTH_API_URI_PATH.concat("/signin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountCredentialsDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("userName").value(tokenDTO.getUserName()))
                .andExpect(jsonPath("authenticate").value(tokenDTO.getAuthenticate()))
                .andExpect(jsonPath("create").value(tokenDTO.getCreate()))
                .andExpect(jsonPath("expiration").value(tokenDTO.getExpiration()))
                .andExpect(jsonPath("accesToken").value(tokenDTO.getAccesToken()))
                .andExpect(jsonPath("refreshToken").value(tokenDTO.getRefreshToken()));
    }

    @Test
    @DisplayName("Deve retornar status 200 e um novo token ao fazer o refresh do token com parâmetros válidos")
    void refreshTokenTest() throws Exception {
        String username = "example@example.com";
        String refreshToken = "refresh_token";

        when(authServices.refreshToken(any(), any())).thenReturn(tokenDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(AUTH_API_URI_PATH.concat("/refresh/") + username)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", refreshToken);

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("userName").value(tokenDTO.getUserName()))
                .andExpect(jsonPath("authenticate").value(tokenDTO.getAuthenticate()))
                .andExpect(jsonPath("create").value(tokenDTO.getCreate()))
                .andExpect(jsonPath("expiration").value(tokenDTO.getExpiration()))
                .andExpect(jsonPath("accesToken").value(tokenDTO.getAccesToken()))
                .andExpect(jsonPath("refreshToken").value(tokenDTO.getRefreshToken()));
    }


    private void startAuth() {
        tokenDTO = new TokenDTO("ana@email.com", true, new Date(),
                new Date(System.currentTimeMillis() + 3600000), "token", "reflesh token");
    }
}
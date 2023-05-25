package com.api.clientes.resources;

import com.api.clientes.Service.UsuarioService;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.security.jwt.JwtTokenProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.api.clientes.resources.util.JsonConvertionUtils.asJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioResourseTest {

    private static final String USUARIO_API_URI_PATH = "/usuarios";

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BCryptPasswordEncoder encoder;

    @MockBean
    UsuarioService usuarioService;

    private Usuario usuario;
    private String accessToken;

    @BeforeEach
    void setUp() {
        start();
        accessToken = obterToken();
    }

    @Test
    @DisplayName("Deve salvar um usuário")
    void insert() throws Exception {

        Map<String, String> usuarioInsert = new HashMap<>();

        usuarioInsert.put("username", "ana@email.com");
        usuarioInsert.put("nome", "Ana Maria");
        usuarioInsert.put("password", "123");

        when(usuarioService.insert(any())).thenReturn(usuario);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API_URI_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuarioInsert));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("username").value(usuario.getUsername()))
                .andExpect(jsonPath("password").value(usuario.getPassword()))
                .andExpect(jsonPath("perfis[0]").value("USER"));
        Mockito.verify(usuarioService, Mockito.times(0)).insert(usuario);
    }

    @Test
    @DisplayName("Deve lançar erro de valiação quando não houver dados suficientes.")
    public void validationExceptionWhenInsufficientDataTest() throws Exception {

        Map<String, String> usuarioInsert = new HashMap<>();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API_URI_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuarioInsert));
        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("erros", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.erros[*]", Matchers.containsInAnyOrder(
                        "O campo nome é obrigatório", "O campo login é obrigatório",
                        "O campo senha é obrigatório")));
    }

    @Test
    @DisplayName("Deve buscar um usuario por id")
    void findById() throws Exception {
        int id = 1;
        when(usuarioService.findById(id)).thenReturn(usuario);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(USUARIO_API_URI_PATH.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("username").value(usuario.getUsername()))
                .andExpect(jsonPath("password").value(usuario.getPassword()))
                .andExpect(jsonPath("perfis[0]").value("USER"));
        Mockito.verify(usuarioService, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve buscar uma página de usuarios ")
    void findPageTest() throws Exception {

        Usuario usuario2 =  new Usuario(2, "paulo@email.com", "Paulo", "123");
        List<Usuario> usuarioList = Arrays.asList(usuario, usuario2);
        Page<Usuario> usuarioPage = new PageImpl<>(usuarioList);

        when(usuarioService.findPage(anyInt(), anyInt(), anyString(),anyString())).thenReturn(usuarioPage);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(USUARIO_API_URI_PATH.concat("/page"))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .param("page", "0")
                .param("size", "5")
                .param("direction", "ASC")
                .param("orderBy", "nome");;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].id").value(usuario.getId()))
                .andExpect(jsonPath("content[0].username").value(usuario.getUsername()))
                .andExpect(jsonPath("content[0].nome").value(usuario.getNome()))
                .andExpect(jsonPath("content[0].password").value(usuario.getPassword()))
                .andExpect(jsonPath("content[0].perfis[0]").value("USER"));
           }


    @Test
    @DisplayName("Deve adicionar o perfil admin no usuário ")
    void addAdmin() throws Exception {

        usuario.addPerfil(Perfil.ADMIN);
        when(usuarioService.addAdmin(anyInt())).thenReturn(usuario);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API_URI_PATH.concat("/perfil-admin/1"))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken);
        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(usuario.getId()))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("username").value(usuario.getUsername()))
                .andExpect(jsonPath("perfis[*]").value(Matchers.containsInAnyOrder("USER", "ADMIN")));
    }

    private String obterToken() {
        return tokenProvider.createAccessToken(usuario.getUsername(), usuario.getNome(), usuario.getPerfis()
                        .stream().map(x -> Perfil.toEnum(x.getCod()).getDescricao()).collect(Collectors.toList()))
                .getAccesToken();
    }

    private void start() {
        usuario = new Usuario(1, "ana@email.com", "Ana Maria", "123");

    }


}
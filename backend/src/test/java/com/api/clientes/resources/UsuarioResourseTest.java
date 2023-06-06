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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.api.clientes.resources.util.JsonConvertionUtils.asJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
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

        // Cria um mapa com os campos obrigatórios
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
                        "O campo nome é obrigatório", "O campo email é obrigatório",
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


    @Test
    @DisplayName("Deve atualizar um usuário")
    void update() throws Exception {
        // Arrange
        int id = 1;

        Map<String, String> usuarioAtualizado = new HashMap<>();

        usuarioAtualizado.put("username", "ana@email.com");
        usuarioAtualizado.put("nome", "Ana Maria");

        when(usuarioService.update(any(), anyInt())).thenReturn(usuario);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USUARIO_API_URI_PATH.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuarioAtualizado))
                .header("Authorization", "Bearer " + accessToken);


        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("username").value(usuario.getUsername()))
                .andExpect(jsonPath("password").value(usuario.getPassword()));
       // Mockito.verify(usuarioService, Mockito.times(1)).update(usuario, id);
    }

    @Test
    @DisplayName("Deve alterar a senha do usuário")
    void changePassword() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();

        request.put("senhaAtual", "MinhaSenha#1");
        request.put("novaSenha", "NovaSeha*1");
        request.put("confirmaNovaSenha", "NovaSenha*1");
        request.put("email", "ana@email.com");

        // Configurar o comportamento do serviço
        doNothing().when(usuarioService).changePassword(any());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(USUARIO_API_URI_PATH.concat("/alterar-senha"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .header("Authorization", "Bearer " + accessToken);

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
        Mockito.verify(usuarioService, Mockito.times(1)).changePassword(request);
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo nome de usuário")
    void findByUsername() throws Exception {
        // Arrange
        String username = "usuario";

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername(username);
        usuario.setNome("Nome do Usuário");

        when(usuarioService.findByUsername(username)).thenReturn(usuario);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(USUARIO_API_URI_PATH.concat("/username/" + username))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(usuario.getId()))
                .andExpect(jsonPath("username").value(usuario.getUsername()))
                .andExpect(jsonPath("nome").value(usuario.getNome()));
    }

    @Test
    @DisplayName("Deve excluir um usuário pelo ID")
    void deleteById() throws Exception {
        // Arrange
        int id = 1;

        doNothing().when(usuarioService).deleteById(id);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(USUARIO_API_URI_PATH.concat("/" + id))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Act & Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
        verify(usuarioService, times(1)).deleteById(id);
    }

    private void start() {
        usuario = new Usuario(1, "ana@email.com", "Ana Maria", "123");

    }


}
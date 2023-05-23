package com.api.clientes.resources;

import com.api.clientes.Service.ClienteService;
import com.api.clientes.dto.security.TokenDTO;
import com.api.clientes.model.entity.Cliente;
import com.api.clientes.security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.api.clientes.resources.util.JsonConvertionUtils.asJsonString;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteResourseTest {

    @MockBean
    private ClienteService clienteService;

    @InjectMocks
    private ClienteResourse clienteResourse;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    JwtTokenProvider provider;

    private Cliente cliente;
    private static final String CLIENTE_API_URI_PATH = "/clientes";
    private String accessToken;

    @BeforeEach
    void setUp() {
        start();
        accessToken = obterToken();
    }

    @Test
    @DisplayName("Deve salvar um cliente")
    void insertTest() throws Exception {

        when(clienteService.insert(any())).thenReturn(cliente);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CLIENTE_API_URI_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ".concat(accessToken))
                .content(asJsonString(cliente));

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(cliente.getId()))
                .andExpect(jsonPath("nome").value(cliente.getNome()))
                .andExpect(jsonPath("dataCadastro").value(cliente.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("cpf").value(cliente.getCpf()))
        ;
    }

    @Test
    @DisplayName("Deve dar erro se os dados obrigatórios não forem informados")
    void insertNotTest() throws Exception {

        when(clienteService.insert(any())).thenReturn(cliente);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CLIENTE_API_URI_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ".concat(accessToken))
                .content(asJsonString(new Cliente()));

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("erros", hasSize(2)))
                .andExpect(jsonPath("erros[*]", containsInAnyOrder(
                        "O campo cpf é obrigatório.!!", "O campo nome é obrigatório.!!")));
    }

    @Test
    @DisplayName("Deve alterar os dados de um cliente")
    void update() throws Exception {
        int id = 1;
        cliente.setNome("Ana Clara");
        cliente.setCpf("02575618622");
        when(clienteService.update(any(), anyInt())).thenReturn(cliente);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(CLIENTE_API_URI_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(cliente))
                .header("Authorization", "Bearer ".concat(accessToken));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(cliente.getId()))
                .andExpect(jsonPath("nome").value("Ana Clara"))
                .andExpect(jsonPath("dataCadastro").value(cliente.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("cpf").value("02575618622"))
        ;
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    void deleteTest() throws Exception {
        int id = 1;
        doNothing().when(clienteService).delete(anyInt());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(CLIENTE_API_URI_PATH.concat("/" + id))
                .header("Authorization", "Bearer ".concat(accessToken));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent());
        verify(clienteService, times(1)).delete(id);
    }

    @Test
    @DisplayName("Deve buscar um usuario por id")
    void findByIdTest() throws Exception {
        int id = 1;
        when(clienteService.find(anyInt())).thenReturn(cliente);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CLIENTE_API_URI_PATH.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ".concat(accessToken));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(cliente.getId()))
                .andExpect(jsonPath("nome").value(cliente.getNome()))
                .andExpect(jsonPath("dataCadastro").value(cliente.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("cpf").value(cliente.getCpf()));
        verify(clienteService, times(1)).find(id);

    }

    @Test
    @DisplayName("Deve buscar uma lista de clientes")
    void findAllTest() throws Exception {

        Cliente cliente2 = new Cliente(2, "Rafaela", "02578919622", LocalDate.now());
        List<Cliente> clientes = Arrays.asList(cliente, cliente2);

        when(clienteService.findAll()).thenReturn(clientes);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CLIENTE_API_URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ".concat(accessToken));
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(cliente.getId()))
                .andExpect(jsonPath("[0].nome").value(cliente.getNome()))
                .andExpect(jsonPath("[0].dataCadastro").value(cliente.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("[0].cpf").value(cliente.getCpf()))
                .andExpect(jsonPath("[1].id").value(cliente2.getId()))
                .andExpect(jsonPath("[1].nome").value(cliente2.getNome()))
                .andExpect(jsonPath("[1].dataCadastro").value(cliente2.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("[1].cpf").value(cliente2.getCpf()))
                .andExpect(jsonPath("$", hasSize(2)))
        ;
        verify(clienteService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma pagina de clientes")
    void findPageTest() throws Exception {
        Cliente cliente2 = new Cliente(2, "Rafaela", "02578919622", LocalDate.now());
        List<Cliente> clientes = Arrays.asList(cliente, cliente2);

        Page<Cliente> pageClientes = new PageImpl<>(clientes);

        when(clienteService.findPage(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageClientes);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CLIENTE_API_URI_PATH.concat("/page"))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ".concat(accessToken))
                .param("page", "0")
                .param("size", "5")
                .param("direction", "ASC")
                .param("orderBy", "nome");
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].id").value(cliente.getId()))
                .andExpect(jsonPath("content[0].nome").value(cliente.getNome()))
                .andExpect(jsonPath("content[0].dataCadastro").value(cliente.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("content[0].cpf").value(cliente.getCpf()))
                .andExpect(jsonPath("content[1].id").value(cliente2.getId()))
                .andExpect(jsonPath("content[1].nome").value(cliente2.getNome()))
                .andExpect(jsonPath("content[1].dataCadastro").value(cliente2.getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("content[1].cpf").value(cliente2.getCpf()))
                .andExpect(jsonPath("size").value(2));
    }

    private void start() {
        cliente = new Cliente(1, "Paula", "09038102631", LocalDate.now());
    }

    private String obterToken() {
        TokenDTO tokenDTO = provider.createAccessToken("ana@email.com", "ana",
                Arrays.asList("USER", "ADMIN"));
        return tokenDTO.getAccesToken();
    }
}
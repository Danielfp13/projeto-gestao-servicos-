package com.api.clientes.resources;

import com.api.clientes.Service.ServicoPrestadoService;
import com.api.clientes.dto.ServicoPrestadoDto;
import com.api.clientes.dto.security.TokenDTO;
import com.api.clientes.model.entity.Cliente;
import com.api.clientes.model.entity.ServicoPrestado;
import com.api.clientes.security.jwt.JwtTokenProvider;
import org.hamcrest.Matchers;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.api.clientes.resources.util.JsonConvertionUtils.asJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServicoPrestadoResourceTest {

    @Autowired
    private JwtTokenProvider provider;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    ServicoPrestadoResource servicoPrestadoResource;

    @MockBean
    ServicoPrestadoService servicoPrestadoService;

    private TokenDTO tokenDTO;
    private String accessToken;
    private ServicoPrestadoDto servicoPrestadoDto;
    private ServicoPrestado servicoPrestado;
    private Cliente cliente;

    private static final String SERVICO_API_URI_PATH = "/servicos-prestado";

    @BeforeEach
    void setUp() {
        start();
        accessToken = obterToken();
    }

    @Test
    @DisplayName("Deve salvar um serviço")
    void insert() throws Exception {
        when(servicoPrestadoService.insert(any())).thenReturn(servicoPrestado);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(SERVICO_API_URI_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .content(asJsonString(servicoPrestadoDto));

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(servicoPrestado.getId()))
                .andExpect(jsonPath("descricao").value(servicoPrestado.getDescricao()))
                .andExpect(jsonPath("data").value(servicoPrestado.getData()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                .andExpect(jsonPath("valor").value(servicoPrestado.getValor()))
                .andExpect(jsonPath("cliente.id").value(servicoPrestado.getCliente().getId()))
                .andExpect(jsonPath("cliente.cpf").value(servicoPrestado.getCliente().getCpf()))
                .andExpect(jsonPath("cliente.nome").value(servicoPrestado.getCliente().getNome()))
                .andExpect(jsonPath("cliente.dataCadastro").value(servicoPrestado.getCliente().getDataCadastro()
                        .format(DateTimeFormatter.ofPattern("dd-MM-YYYY"))));
    }

    @Test
    @DisplayName("Deve lançar erro de valiação quando não houver dados suficientes.")
    public void validationExceptionWhenInsufficientDataTest() throws Exception {

        ServicoPrestado newServicoPrestado = new ServicoPrestado();

        when(servicoPrestadoService.insert(any())).thenReturn(servicoPrestado);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(SERVICO_API_URI_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(newServicoPrestado))
                .header("Authorization", "Bearer " + accessToken);
        mockMvc
                .perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("erros", Matchers.hasSize(4)))
                .andExpect(jsonPath("erros[*]", Matchers.containsInAnyOrder(
                        "O campo cliente é obrigatorio!", "O campo descrição é obrigatorio!",
                        "O campo preço é obrigatorio!", "O campo data é obrigatorio!")));
    }

    @Test
    @DisplayName("Deve retornar a página de serviços prestados com base nos parâmetros fornecidos")
    void shouldReturnPageOfServicosPrestados() throws Exception {

        ServicoPrestado servico1 = new ServicoPrestado(1, "Serviço 1", cliente, BigDecimal.valueOf(100.0), LocalDate.now());
        ServicoPrestado servico2 = new ServicoPrestado(2, "Serviço 2", cliente, BigDecimal.valueOf(150.0), LocalDate.now());
        List<ServicoPrestado> servicosPrestados = Arrays.asList(servico1, servico2);

        // Criação da página de resultado
        Page<ServicoPrestado> pageServicoPrestado = new PageImpl<>(servicosPrestados);

        // Configuração do mock do ServicoPrestadoService
        when(servicoPrestadoService.findByNameClienteAndMonth(anyString(), anyString(), anyInt(), anyInt(),
                anyString(), anyString()))
                .thenReturn(pageServicoPrestado);

        // Execução do teste
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(SERVICO_API_URI_PATH.concat("/search"))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .param("nome", "daniel")
                .param("mes", "4")
                .param("page", String.valueOf("0"))
                .param("size", String.valueOf("5"))
                .param("direction", "ASC")
                .param("orderBy", "id");
        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].id").value(servico1.getId()))
                .andExpect(jsonPath("content[0].descricao").value(servico1.getDescricao()))
                .andExpect(jsonPath("content[0].valor").value(servico1.getValor()))
                .andExpect(jsonPath("content[0].cliente.id").value(servico1.getCliente().getId()))
                .andExpect(jsonPath("content[0].cliente.nome").value(servico1.getCliente().getNome()))
                .andExpect(jsonPath("content[0].cliente.cpf").value(servico1.getCliente().getCpf()))
                .andExpect(jsonPath("content[1].id").value(servico2.getId()))
                .andExpect(jsonPath("content[1].descricao").value(servico2.getDescricao()))
                .andExpect(jsonPath("size").value(2));
    }

    private String obterToken() {
        tokenDTO = provider.createAccessToken("ana@email.com", "Ana Maria", Arrays.asList("USER"));
        return tokenDTO.getAccesToken();
    }

    private void start() {

        tokenDTO = new TokenDTO("ana@email.com", true, new Date(),
                new Date(System.currentTimeMillis() + 3600000), "access_token", "reflesh_token");
        servicoPrestadoDto = new ServicoPrestadoDto("trabalhar", "R$23.90", "2023-09-12", 1);
        cliente = new Cliente(1, "daniel", "09038102631", LocalDate.now());
        servicoPrestado = new ServicoPrestado(1, "trabalhar", cliente, BigDecimal.valueOf(23.90), LocalDate.now());
    }


}
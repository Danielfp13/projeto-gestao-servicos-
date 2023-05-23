package com.api.clientes.service;

import com.api.clientes.Service.ServicoPrestadoService;
import com.api.clientes.dto.ServicoPrestadoDto;
import com.api.clientes.model.entity.Cliente;
import com.api.clientes.model.entity.ServicoPrestado;
import com.api.clientes.repository.ClienteRepository;
import com.api.clientes.repository.ServicoPrestadoRepository;
import com.api.clientes.utils.StringToBigdecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServicoPrestadoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicoPrestadoRepository servicoPrestadoRepository;

    @Mock
    private StringToBigdecimal stringToBigdecimal;

    @InjectMocks
    private ServicoPrestadoService servicoPrestadoService;

    private Cliente cliente;
    private ServicoPrestadoDto servicoPrestadoDto;
    private ServicoPrestado servicoPrestado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("João");

        servicoPrestadoDto = new ServicoPrestadoDto();
        servicoPrestadoDto.setDescricao("Descrição do serviço");
        servicoPrestadoDto.setPreco("100.00");
        servicoPrestadoDto.setData("2023-05-19");
        servicoPrestadoDto.setIdCliente(1);

        servicoPrestado = new ServicoPrestado();
        servicoPrestado.setId(1);
        servicoPrestado.setDescricao("Descrição do serviço");
        servicoPrestado.setCliente(cliente);
        servicoPrestado.setValor(BigDecimal.valueOf(100.00));
        servicoPrestado.setData(LocalDate.parse("2023-05-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Test
    @DisplayName("Deve inserir um novo serviço prestado")
    void insert() {
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(cliente));
        when(stringToBigdecimal.convert(anyString())).thenReturn(BigDecimal.valueOf(100.00));
        when(servicoPrestadoRepository.save(any(ServicoPrestado.class))).thenReturn(servicoPrestado);

        ServicoPrestado result = servicoPrestadoService.insert(servicoPrestadoDto);

        assertNotNull(result);
        assertEquals(servicoPrestado.getId(), result.getId());
        assertEquals(servicoPrestado.getDescricao(), result.getDescricao());
        assertEquals(servicoPrestado.getCliente(), result.getCliente());
        assertEquals(servicoPrestado.getValor(), result.getValor());
        assertEquals(servicoPrestado.getData(), result.getData());

        verify(clienteRepository, times(1)).findById(servicoPrestadoDto.getIdCliente());
        verify(stringToBigdecimal, times(1)).convert(servicoPrestadoDto.getPreco());
        verify(servicoPrestadoRepository, times(1)).save(any(ServicoPrestado.class));
    }

    @Test
    @DisplayName("Deve buscar os serviços prestados pelo nome do cliente e mês")
    void findByNameClienteAndMonth() {
        String nome = "João";
        String mes = "05";
        Integer page = 0;
        Integer size = 10;
        String direction = "ASC";
        String orderBy = "descricao";

        List<ServicoPrestado> servicosPrestados = new ArrayList<>();
        servicosPrestados.add(servicoPrestado);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<ServicoPrestado> pageResult = new PageImpl<>(servicosPrestados, pageRequest, servicosPrestados.size());

        when(servicoPrestadoRepository.findByNameClienteAndMonth(anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(pageResult);

        Page<ServicoPrestado> result = servicoPrestadoService.findByNameClienteAndMonth(nome, mes, page, size, direction, orderBy);

        assertNotNull(result);
        assertEquals(servicosPrestados.size(), result.getTotalElements());
        assertEquals(servicosPrestados, result.getContent());

        verify(servicoPrestadoRepository, times(1))
                .findByNameClienteAndMonth("%" + nome + "%", "%" + mes, pageRequest);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao inserir um serviço prestado com cliente inexistente")
    void insert_ClienteInexistente() {
        ServicoPrestadoDto servicoPrestadoDto = new ServicoPrestadoDto();
        servicoPrestadoDto.setIdCliente(1);
        servicoPrestadoDto.setDescricao("Descrição do serviço");
        servicoPrestadoDto.setPreco("100.00");
        servicoPrestadoDto.setData("2023-05-20");

        when(clienteRepository.findById(anyInt())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            servicoPrestadoService.insert(servicoPrestadoDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cliente inexistente.", exception.getReason());

        verify(clienteRepository, times(1)).findById(servicoPrestadoDto.getIdCliente());
        verifyNoInteractions(stringToBigdecimal);
        verifyNoInteractions(servicoPrestadoRepository);
    }
}

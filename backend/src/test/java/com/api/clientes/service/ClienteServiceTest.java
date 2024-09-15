package com.api.clientes.service;

import com.api.clientes.Service.ClienteService;

import com.api.clientes.model.entity.Cliente;
import com.api.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Incluído para não usar banco embutido
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        start();
    }

    @Test
    @DisplayName("Deve inserir um cliente")
    void insert() {

        when(repository.save(any())).thenReturn(cliente);
        Cliente clienteSalvo = service.insert(cliente);

        assertThat(clienteSalvo.getId()).isEqualTo(cliente.getId());
        assertThat(clienteSalvo.getCpf()).isEqualTo(cliente.getCpf());
        assertThat(clienteSalvo.getNome()).isEqualTo(cliente.getNome());
        assertThat(clienteSalvo.getDataCadastro()).isEqualTo(cliente.getDataCadastro());
        verify(repository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Deve buscar um cliente por id")
    void findTest() {
        int id = 1;
        when(repository.findById(anyInt())).thenReturn(Optional.of(cliente));
        Cliente clienteEncontrado = service.find(id);

        assertThat(clienteEncontrado.getId()).isEqualTo(cliente.getId());
        assertThat(clienteEncontrado.getNome()).isEqualTo(cliente.getNome());
        assertThat(clienteEncontrado.getCpf()).isEqualTo(cliente.getCpf());
        assertThat(clienteEncontrado.getDataCadastro()).isEqualTo(cliente.getDataCadastro());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar um erro se o cliente não existir")
    void findObjectNotFound() {
        int id = 1;
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.find(id));
        assertThat("Não existe cliente com id = " + id + ".").isEqualTo(exception.getReason());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar uma lista de clientes")
    void findAll() {
        Cliente cliente2 = new Cliente(2, "Paula", "90987693621", LocalDate.now());
        List<Cliente> clientes = Arrays.asList(cliente, cliente2);

        when(repository.findAll()).thenReturn(clientes);

        List<Cliente> clientesEncontrados = service.findAll();

        assertThat(clientesEncontrados.get(0).getId()).isEqualTo(clientes.get(0).getId());
        assertThat(clientesEncontrados.get(0).getNome()).isEqualTo(clientes.get(0).getNome());
        assertThat(clientesEncontrados.get(0).getCpf()).isEqualTo(clientes.get(0).getCpf());
        assertThat(clientesEncontrados.get(0).getDataCadastro()).isEqualTo(clientes.get(0).getDataCadastro());
        assertThat(clientesEncontrados.get(1).getId()).isEqualTo(clientes.get(1).getId());
        assertThat(clientesEncontrados.get(1).getNome()).isEqualTo(clientes.get(1).getNome());
        assertThat(clientesEncontrados.get(1).getCpf()).isEqualTo(clientes.get(1).getCpf());
        assertThat(clientesEncontrados.get(1).getDataCadastro()).isEqualTo(clientes.get(1).getDataCadastro());
        assertThat(clientesEncontrados.size()).isEqualTo(2);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma página de clientes")
    void findPageTest() {
        Integer page = 0;
        Integer linePerPage = 5;
        String direction = "ASC";
        String orderBy = "nome";
        Cliente cliente2 = new Cliente(2, "Paula", "90987693621", LocalDate.now());
        List<Cliente> clientes = Arrays.asList(cliente, cliente2);

        Page<Cliente> pageCliente = new PageImpl<>(clientes);
        PageRequest pageRequest = PageRequest.of(page, linePerPage, Sort.Direction.valueOf(direction), orderBy);
        when(repository.findAll(pageRequest)).thenReturn(pageCliente);

        Page<Cliente> pageClienteEncontrado = service.findPage(page, linePerPage, direction, orderBy);

        assertThat(pageClienteEncontrado.getContent().get(0).getId()).isEqualTo(pageCliente.getContent().get(0).getId());
        assertThat(pageClienteEncontrado.getContent().get(0).getNome()).isEqualTo(pageCliente.getContent().get(0).getNome());
        assertThat(pageClienteEncontrado.getContent().get(0).getCpf()).isEqualTo(pageCliente.getContent().get(0).getCpf());
        assertThat(pageClienteEncontrado.getContent().get(0).getDataCadastro()).isEqualTo(pageCliente.getContent().get(0).getDataCadastro());
        assertThat(pageClienteEncontrado.getContent().get(1).getId()).isEqualTo(pageCliente.getContent().get(1).getId());
        assertThat(pageClienteEncontrado.getContent().get(1).getNome()).isEqualTo(pageCliente.getContent().get(1).getNome());
        assertThat(pageClienteEncontrado.getContent().get(1).getCpf()).isEqualTo(pageCliente.getContent().get(1).getCpf());
        assertThat(pageClienteEncontrado.getContent().get(1).getDataCadastro()).isEqualTo(pageCliente.getContent().get(1).getDataCadastro());
        assertThat(pageClienteEncontrado.getTotalElements()).isEqualTo(2);
        verify(repository, times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("Deve deletar um cliente")
    void delete() {
        int id = 1;
        when(repository.findById(id)).thenReturn(Optional.of(cliente));
        doNothing().when(repository).deleteById(anyInt());
        service.delete(id);
        verify(repository,times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve retornar um erro ao deletar um cliente")
    void deleteErrorTest() {
        int id = 1;
        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        DataIntegrityViolationException dataIntegrityViolationException = new
                DataIntegrityViolationException("Violação de integridade ao tentar excluir um cliente.");
        doThrow(dataIntegrityViolationException).when(repository).deleteById(id);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.delete(id));
        assertThat("Não se pode excluir clientes com serviços.").isEqualTo(exception.getReason());
        verify(repository,times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve atualizar um cliente")
    void update() {
        int id = 1;
        cliente.setNome("Ana Laura");
        cliente.setCpf("9876542573");
        when(repository.save(any())).thenReturn(cliente);
        when(repository.findById(anyInt())).thenReturn(Optional.of(cliente));
        Cliente clienteSalvo = service.update(cliente, id);

        assertThat(clienteSalvo.getId()).isEqualTo(cliente.getId());
        assertThat(clienteSalvo.getCpf()).isEqualTo(cliente.getCpf());
        assertThat(clienteSalvo.getNome()).isEqualTo(cliente.getNome());
        assertThat(clienteSalvo.getDataCadastro()).isEqualTo(cliente.getDataCadastro());
        verify(repository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Deve definir a data de cadastro antes de persistir o cliente")
    void prePersistTest() {
          // Chama o método prePersist
        cliente.prePersist();

        // Verifica se a data de cadastro foi definida corretamente
        LocalDate dataAtual = LocalDate.now();
        assertThat(dataAtual).isEqualTo(cliente.getDataCadastro());
    }



    void start() {
        cliente = new Cliente(1, "Ana Mara", "02575618622", LocalDate.now());
    }

}

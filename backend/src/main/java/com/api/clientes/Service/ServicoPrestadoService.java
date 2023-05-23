package com.api.clientes.Service;

import com.api.clientes.dto.ServicoPrestadoDto;
import com.api.clientes.model.entity.Cliente;
import com.api.clientes.model.entity.ServicoPrestado;
import com.api.clientes.repository.ClienteRepository;
import com.api.clientes.repository.ServicoPrestadoRepository;
import com.api.clientes.utils.StringToBigdecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ServicoPrestadoService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicoPrestadoRepository servicoPrestadoRepositoty;

    @Autowired
    StringToBigdecimal stringToBigdecimal;

    public ServicoPrestado insert(ServicoPrestadoDto obj) {
        LocalDate data = LocalDate.parse(obj.getData(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Integer idCliente = obj.getIdCliente();

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente inexistente."));

        ServicoPrestado servicoPrestado = new ServicoPrestado(null, obj.getDescricao(), cliente,
                stringToBigdecimal.convert(obj.getPreco()), data);

        return servicoPrestadoRepositoty.save(servicoPrestado);
    }

    public Page<ServicoPrestado> findByNameClienteAndMonth(String nome, String mes, Integer page, Integer size, String direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        return servicoPrestadoRepositoty.findByNameClienteAndMonth(
                "%" + nome + "%", "%" + mes, pageRequest);
    }
}

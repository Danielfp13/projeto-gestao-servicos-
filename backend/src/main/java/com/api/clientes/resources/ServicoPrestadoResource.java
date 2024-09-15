package com.api.clientes.resources;

import com.api.clientes.Service.ServicoPrestadoService;
import com.api.clientes.dto.ServicoPrestadoDto;
import com.api.clientes.model.entity.ServicoPrestado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/servicos-prestado")
@Tag(name = "Servicos Prestado Endpoint", description = "Operações relacionadas aos serviços prestados.")

public class ServicoPrestadoResource {

    @Autowired
    private ServicoPrestadoService servicoPrestadoService;

    @PostMapping
    @Operation(summary = "Cadastro de servico prestado", description = "Cadastra um novo serviço prestado no sistema.")
    public ResponseEntity<ServicoPrestado> insert(@Valid @RequestBody ServicoPrestadoDto servicoPrestadoDto) {
        ServicoPrestado servicoPrestado = servicoPrestadoService.insert(servicoPrestadoDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(servicoPrestado.getId()).toUri();
        return ResponseEntity.created(uri).body(servicoPrestado);
    }

    @GetMapping(value = "/search")
    @Operation(summary = "Busca paginada de serviço prestado",
            description = "Recupera uma lista paginada de serviços prestados cadastrados no sistema. Permite realizar a busca " +
                    "pelos campos nome do cliente e mês de prestação do serviço. Além disso, é possível definir o número da página, " +
                    "a quantidade de registros por página, a direção da ordenação (ASC ou DESC) e o campo de ordenação (ex: nome, id)."
    )
    public ResponseEntity<Page<ServicoPrestado>> search(
            @RequestParam(value = "nome", defaultValue = "", required = false) String nome,
            @RequestParam(value = "mes", defaultValue = "", required = false) String mes,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC", required = false) String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Page<ServicoPrestado> pageServicoPrestado = servicoPrestadoService.findByNameClienteAndMonth(nome, mes, page, size, direction, orderBy);
        return ResponseEntity.ok().body(pageServicoPrestado);
    }
}

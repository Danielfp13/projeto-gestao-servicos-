package com.api.clientes.resources;

import com.api.clientes.Service.ServicoPrestadoService;
import com.api.clientes.dto.ServicoPrestadoDto;
import com.api.clientes.model.entity.ServicoPrestado;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/servicos-prestado")
public class ServicoPrestadoResource {

    @Autowired
    private ServicoPrestadoService servicoPrestadoService;

    @PostMapping
    public ResponseEntity<ServicoPrestado> insert(@Valid @RequestBody ServicoPrestadoDto servicoPrestadoDto) {
        ServicoPrestado servicoPrestado = servicoPrestadoService.insert(servicoPrestadoDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(servicoPrestado.getId()).toUri();
        return ResponseEntity.created(uri).body(servicoPrestado);
    }

    @GetMapping(value = "/search")
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

package com.api.clientes.resources;

import com.api.clientes.Service.ClienteService;
import com.api.clientes.model.entity.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Cliente Endpoint", description = "Operações relacionadas aos clientes.")
public class ClienteResourse {

    @Autowired
    private ClienteService service;

    @PostMapping
    @Operation(summary = "Cadastro de cliente", description = "Cadastra um novo Cliente no sistema")
    public ResponseEntity<Cliente> insert(@Valid @RequestBody Cliente cliente){
        cliente = service.insert(cliente);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cliente.getId()).toUri();
        return ResponseEntity.created(uri).body(cliente);
    }

    @PutMapping ("/{id}")
    @Operation(summary = "Alterar cliente", description = "Altera os dados de um cliente previamente cadastrado no sistema.")
    public ResponseEntity<Cliente> update(@Valid @RequestBody Cliente cliente, @PathVariable Integer id){
        cliente =service.update(cliente,id);
        return ResponseEntity.ok().body(cliente);
    }

    @DeleteMapping ("/{id}")
    @Operation(summary = "Excluir cliente", description = "Exclui um cliente já cadastrado no sistema.")
    public ResponseEntity<Cliente> delete(@PathVariable Integer id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping ("/{id}")
    @Operation(summary = "Consultar Cliente por ID", description = "Recupera os dados de um cliente previamente cadastrado" +
            " no sistema, utilizando o identificador único (ID) fornecido.")
    public ResponseEntity<Cliente> find(@PathVariable Integer id){
        Cliente cliente = service.find(id);
        return ResponseEntity.ok().body(cliente);
    }

    @GetMapping
    @Operation(summary = "Consultar todos os cliente", description = "Recupera os detalhes de todos os  cliente previamente " +
            "cadastrado no sistema.")
    public ResponseEntity<List<Cliente>> findAll(){
        List<Cliente> clientes = service.findAll();
        return ResponseEntity.ok().body(clientes);
    }


    @GetMapping("/page")
    @Operation(summary = "Busca paginada de clientes", description = "Recupera uma lista paginada de clientes cadastrados " +
            "no sistema. Permite a definição do número da página, quantidade de registros por página, direção da ordenação " +
            "(ASC ou DESC) e campo de ordenação (ex: nome)."
    )
    public ResponseEntity<Page<Cliente>> findPage(
            @RequestParam(value = "page" , defaultValue = "0") Integer page,
            @RequestParam(value = "linePerPage" , defaultValue = "24") Integer linePerPage,
            @RequestParam(value = "direction" , defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy" , defaultValue = "nome") String orderBy
    ){
        Page<Cliente> pageCliente = service.findPage(page,linePerPage,direction,orderBy);
        return ResponseEntity.ok().body(pageCliente);
    }

}
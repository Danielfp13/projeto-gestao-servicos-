package com.api.clientes.resources;

import com.api.clientes.Service.UsuarioService;
import com.api.clientes.model.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/usuarios")
//@CrossOrigin("http://localhost:4200")
public class UsuarioResourse {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<Usuario> insert(@Valid @RequestBody Usuario usuario) {
        usuario = service.insert(usuario);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PostMapping("/perfil-admin/{id}")
    public ResponseEntity<Usuario> addAdmin(@PathVariable Integer id) {
        Usuario usuario = service.addAdmin(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario>findById(@PathVariable Integer id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/page")
    public  ResponseEntity<Page<Usuario>>findPage(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "linePerPage", defaultValue = "10", required = false) Integer linePerPage,
            @RequestParam(value = "direction", defaultValue = "ASC", required = false) String direction,
            @RequestParam(value = "ordeBy", defaultValue = "nome", required = false) String orderBy
    ){
        
        
        
        
        
        
        
        
        
        
        return ResponseEntity.ok(service.findPage(page,linePerPage,direction,orderBy));
    }
}
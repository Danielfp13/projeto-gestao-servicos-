package com.api.clientes.resources;

import com.api.clientes.Service.UsuarioService;
import com.api.clientes.dto.ChangePasswordRequestDTO;
import com.api.clientes.model.entity.Cliente;
import com.api.clientes.model.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;


@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuario Endpoint", description = "Operações relacionadas a usuário.")
public class UsuarioResourse {

    @Autowired
    private UsuarioService service;

    @PostMapping
    @Operation(summary = "Cadastro de usuário", description = "Cadastra um novo usuário no sistema.")
    public ResponseEntity<Usuario> insert( @Validated(Usuario.Insercao.class) @RequestBody Usuario usuario) {
        usuario = service.insert(usuario);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PutMapping ("/{id}")
    @Operation(summary = "Alterar usuário", description = "Altera os dados de um usuário previamente cadastrado no sistema.")
    public ResponseEntity<Usuario> update(@Validated(Usuario.Atualizacao.class) @RequestBody Usuario usuario, @PathVariable Integer id){
        usuario = service.update(usuario,id);
        return ResponseEntity.ok().body(usuario);
    }

    @PostMapping ("/alterar-senha")
    @Operation(summary = "Alterar senha",
            description = "Permite a alteração da senha de um usuário previamente cadastrado no sistema informando os seguintes " +
                    "campos senhaAtual, novaSenha, confirmaNovaSenha e email.")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        service.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/perfil-admin/{id}")
    @Operation(summary = "Adicionar perfil ADMIN", description = "Adiciona o perfil de adiministrador a um usuário informando o id do usuário.")
    public ResponseEntity<Usuario> addAdmin(@PathVariable Integer id) {
        Usuario usuario = service.addAdmin(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/remover-perfil-admin/{id}")
    @Operation(summary = "Remove o perfil ADMIN", description = "Remove o perfil de adiministrador a um usuário informando o id do usuário.")
    public ResponseEntity<Usuario> removeAdmin(@PathVariable Integer id) {
        Usuario usuario = service.removeAdmin(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar Usuário por ID", description = "Recupera os dados de um usuário previamente cadastrado" +
            " no sistema, utilizando o identificador único (ID) fornecido.")
    public ResponseEntity<Usuario>findById(@PathVariable Integer id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Consultar Usuário por username", description = "Recupera os dados de um usuário previamente cadastrado" +
            " no sistema, utilizando o identificador o email fornecido.")
    public ResponseEntity<Usuario>findByUsername(@PathVariable String username){
        return ResponseEntity.ok(service.findByUsername(username));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um usuário", description = "Exclui um usuário já cadastrado no sistema, informando o id do usuário.")
    public ResponseEntity<Usuario>deleteById(@PathVariable Integer id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    @Operation(summary = "Busca paginada de usuários", description = "Recupera uma lista paginada de usuários cadastrados " +
            "no sistema. Permite a definição do número da página, quantidade de registros por página, direção da ordenação " +
            "(ASC ou DESC) e campo de ordenação (ex: nome)."
    )
    public  ResponseEntity<Page<Usuario>>findPage(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "linePerPage", defaultValue = "10", required = false) Integer linePerPage,
            @RequestParam(value = "direction", defaultValue = "ASC", required = false) String direction,
            @RequestParam(value = "orderBy", defaultValue = "nome", required = false) String orderBy
    ){
        return ResponseEntity.ok(service.findPage(page,linePerPage,direction,orderBy));
    }
}
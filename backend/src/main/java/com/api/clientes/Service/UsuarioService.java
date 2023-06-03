package com.api.clientes.Service;

import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UsuarioRepository repositoty;

    public Usuario insert(Usuario usuario) {
        if (existsByUsername(usuario.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe usuário com o login " + usuario.getUsername() + " cadastrado.");
        } else {
            usuario.setId(null);
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            return repositoty.save(usuario);
        }
    }

    public boolean existsByUsername(String username) {
        return repositoty.existsByUsername(username);
    }


    public Usuario findById(Integer id) {
        return repositoty.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Não existe usuario com esse id " + id + "."));
    }

    public Usuario addAdmin(Integer id) {
        Usuario usuario = findById(id);
        if (!usuario.getPerfis().contains(Perfil.ADMIN)) {
            usuario.addPerfil(Perfil.ADMIN);
            usuario = repositoty.save(usuario);
        }
        return usuario;
    }

    public Usuario findByUsername(String email) {
        return repositoty.findByUsername(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O email fornecido (" + email + ") não está cadastrado."));
    }

    public void updatePassword(Usuario usuario, String password) {
        usuario = this.findByUsername(usuario.getUsername());
        usuario.setPassword(encoder.encode(password));
        repositoty.save(usuario);
    }

    public Page<Usuario> findPage(Integer page, Integer linePerPage, String direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page,linePerPage, Sort.Direction.valueOf(direction),orderBy);
        return repositoty.findAll(pageRequest);
    }
}

package com.api.clientes.Service;

import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UsuarioRepository repository;

    public Usuario insert(Usuario usuario) {
        if (existsByUsername(usuario.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe usuário com o login " + usuario.getUsername() + " cadastrado.");
        } else {
            isValidPassword(usuario.getPassword());
            usuario.setId(null);
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            return repository.save(usuario);
        }
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }


    public Usuario findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Não existe usuário com esse id " + id + "."));

    }

    public Usuario addAdmin(Integer id) {
        Usuario usuario = findById(id);
        if (!usuario.getPerfis().contains(Perfil.ADMIN)) {
            usuario.addPerfil(Perfil.ADMIN);
            usuario = repository.save(usuario);
        }
        return usuario;
    }

    public Usuario findByUsername(String email) {
        return repository.findByUsername(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O email fornecido (" + email + ") não está cadastrado."));
    }


    public Page<Usuario> findPage(Integer page, Integer linePerPage, String direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, linePerPage, Sort.Direction.valueOf(direction), orderBy);
        return repository.findAll(pageRequest);
    }

    public void deleteById(Integer id) {
        this.findById(id);
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conta não pode ser excluida");
        }
    }

    public Usuario update(Usuario usuario, Integer id) {
        Optional<Usuario> existingUsuario = repository.findByUsername(usuario.getUsername());
        if (existingUsuario.isPresent()) {
            if (!existingUsuario.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O e-mail já está sendo usado por outro usuário");
            }
        }
        Usuario newUsuario = this.findById(id);
        BeanUtils.copyProperties(usuario, newUsuario, "id", "password");
        return repository.save(newUsuario);
    }

    public void updatePassword(Usuario usuario, String password) {
        isValidPassword(password);
        usuario = this.findByUsername(usuario.getUsername());
        usuario.setPassword(encoder.encode(password));
        repository.save(usuario);
    }

    public void changePassword(Map<String, String> request) {
        String senhaAtual = request.get("senhaAtual");
        String novaSenha = request.get("novaSenha");
        String confirmaNovaSenha = request.get("confirmaNovaSenha");
        String email = request.get("email");
        if (!novaSenha.equals(confirmaNovaSenha)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A confirmação da nova senha não corresponde à nova senha");
        }
        Usuario usuario = findByUsername(email);

        if (!encoder.matches(senhaAtual, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A senha atual fornecida está incorreta");
        }
        this.updatePassword(usuario, novaSenha);
    }

    public boolean isValidPassword(String password) {
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%¨&*()\\_\\-+=§/?°´{\\[ª\\]}º|\\\\<>.;,]).{6,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha deve atender aos seguintes requisitos:\n" +
                    "- Pelo menos uma letra maiúscula\n" +
                    "- Pelo menos uma letra minúscula\n" +
                    "- Pelo menos um número\n" +
                    "- Pelo menos um dos seguintes caracteres especiais: !@#$%¨&*()_-+=§/?°´{[ª]}º|\\<>.,;");
        }
        return true;
    }
}

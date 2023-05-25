package com.api.clientes.service;

import com.api.clientes.Service.UsuarioService;
import com.api.clientes.model.entity.Usuario;
import com.api.clientes.model.enums.Perfil;
import com.api.clientes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UsuarioServiceTest {


    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    UsuarioService usuarioService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        start();
    }

    @Test
    @DisplayName("Deve inserir um usuario")
    void insertUsuarioTest() {
        when(usuarioRepository.save(Mockito.any())).thenReturn(usuario);
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("senhaCodificada");


        Usuario usuarioSalvo = usuarioService.insert(usuario);

        assertThat(usuarioSalvo.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioSalvo.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioSalvo.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(usuarioSalvo.getPerfis()).isEqualTo(usuario.getPerfis());
    }

    @Test
    @DisplayName("Deve lançar um erro quando o email já estiver cadastrado")
    void insertUsuarioLoginExistenteTest() {

        when(usuarioRepository.existsByUsername(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.insert(usuario));

        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(exception.getStatusCode());
        assertThat("Já existe usuário com o login " + usuario.getUsername() + "cadastrado")
                .isEqualTo(exception.getReason());
    }

    @Test
    @DisplayName("Deve buscar um usuário por id")
    void findByIdTest() {
        int id = 1;

        when(usuarioRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usuario));

        Usuario usuarioEncontrado = usuarioService.findById(id);

        assertThat(usuarioEncontrado.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioEncontrado.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioEncontrado.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(usuarioEncontrado.getPassword()).isEqualTo(usuario.getPassword());
        assertThat(usuarioEncontrado.getPerfis()).isEqualTo(usuario.getPerfis());
    }

    @Test
    @DisplayName("Deve buscar uma pagina de usuario")
    void findPageTest() {
        Integer page = 0;
        Integer linePerPage = 5;
        String direction = "ASC";
        String orderBy = "nome";

        Usuario usuario2 =  new Usuario(2, "paulo@email.com", "Paulo", "123");
        List<Usuario> usuarioList = Arrays.asList(usuario, usuario2);
        Page<Usuario> usuarioPage = new PageImpl<>(usuarioList);

        PageRequest pageRequest = PageRequest.of(page, linePerPage, Sort.Direction.valueOf(direction), orderBy);


        when(usuarioRepository.findAll(pageRequest)).thenReturn(usuarioPage);

        Page<Usuario> pageEncontrado = usuarioService.findPage(page, linePerPage, direction, orderBy);

        assertThat(pageEncontrado.getContent().get(0).getId()).isEqualTo(usuario.getId());
        assertThat(pageEncontrado.getContent().get(0).getNome()).isEqualTo(usuario.getNome());
        assertThat(pageEncontrado.getContent().get(0).getUsername()).isEqualTo(usuario.getUsername());
        assertThat(pageEncontrado.getContent().get(0).getPassword()).isEqualTo(usuario.getPassword());
        assertThat(pageEncontrado.getContent().get(0).getPerfis()).isEqualTo(usuario.getPerfis());
    }

    @Test
    @DisplayName("Deve lançar um erro quando o usuário não existir.")
    void findByIdObjectnotFound() {
        int id = 3;
        when(usuarioRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usuarioService.findById(id));

        assertThat(HttpStatus.NOT_FOUND).isEqualTo(exception.getStatusCode());
        assertThat("Não existe usuario com esse id " + id + ".").isEqualTo(exception.getReason());
    }

    @Test
    @DisplayName("Deve adicionar o perfil ADMIN")
    void addAdminTest() {
        int id = 1;

        when(usuarioRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(Mockito.any())).thenReturn(usuario);

        Usuario usuarioComPerfilAdmin = usuarioService.addAdmin(id);

        assertThat(usuarioComPerfilAdmin.getPerfis()).contains(Perfil.ADMIN);
        Mockito.verify(usuarioRepository, times(1)).findById(id);
        Mockito.verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Não deve adicionar o perfil ADMIN se o usuário já tiver esse perfil")
    void addAdminUsuarioJaPossuiPerfilAdminTest() {
        int id = 1;
        usuario.addPerfil(Perfil.ADMIN);
        when(usuarioRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usuario));

        Usuario usuarioComPerfilAdmin = usuarioService.addAdmin(id);

        assertThat(usuarioComPerfilAdmin.getPerfis()).contains(Perfil.ADMIN);
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    @DisplayName("Deve buscar um usuario pelo email")
    void findByUsernameTest() {
        String email = "ana@email.com.br";
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(usuario));

        Usuario usuarioEncontrado = usuarioService.findByUsername(email);

        assertThat(usuarioEncontrado.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioEncontrado.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioEncontrado.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(usuarioEncontrado.getPassword()).isEqualTo(usuario.getPassword());
        assertThat(usuarioEncontrado.getPerfis()).isEqualTo(usuario.getPerfis());
        Mockito.verify(usuarioRepository, times(1)).findByUsername(email);
    }

    @Test
    @DisplayName("Deve retornar um erro se usuario não existir")
    void findByUsernameNotFound() {
        String email = "ana@email.com";
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class , () ->
                usuarioService.findByUsername(email));
        assertThat("O email fornecido (" + email + ") não está cadastrado.").isEqualTo(exception.getReason());
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(exception.getStatusCode());
        verify(usuarioRepository, times(1)).findByUsername(email);
    }

    @Test
    @DisplayName("Deve alterar a senha do usuario")
    void updatePassword() {
        String novaSenha = "senha-nova";
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(usuario));
        when(encoder.encode(anyString())).thenReturn(novaSenha);
        when(usuarioRepository.save(any())).thenReturn(usuario);
        usuarioService.updatePassword(usuario, novaSenha);

        assertThat(usuario.getPassword()).isEqualTo(novaSenha);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(encoder,times(1)).encode(novaSenha);
        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
    }

    private void start() {
        usuario = new Usuario(null, "ana@email.com.br", "Ana Maria", "123");
    }

}
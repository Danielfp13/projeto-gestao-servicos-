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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        when(encoder.encode(anyString())).thenReturn("SenhaModficada+-3");


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
        assertThat("Já existe usuário com o login " + usuario.getUsername() + " cadastrado.")
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
        assertThat("Não existe usuário com esse id " + id + ".").isEqualTo(exception.getReason());
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
        String novaSenha = "sEnhE-nOvA12";
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(usuario));
        when(encoder.encode(anyString())).thenReturn(novaSenha);
        when(usuarioRepository.save(any())).thenReturn(usuario);
        usuarioService.updatePassword(usuario, novaSenha);

        assertThat(usuario.getPassword()).isEqualTo(novaSenha);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(encoder,times(1)).encode(novaSenha);
        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
    }

    @Test
    @DisplayName("Deve excluir um usuário por ID")
    void deleteByIdTest() {
        Integer id = 1;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        usuarioService.deleteById(id);
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar um erro ao excluir um usuário inexistente")
    void deleteByIdUserNotFoundTest() {
        Integer id = 1;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.deleteById(id));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Não existe usuário com esse id " + id + ".");
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, never()).deleteById(anyInt());

   }

    @Test
    @DisplayName("Deve lançar um erro ao excluir um usuário com restrição de integridade de dados")
    void deleteByIdDataIntegrityViolationTest() {
        Integer id = 1;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        doThrow(DataIntegrityViolationException.class).when(usuarioRepository).deleteById(id);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.deleteById(id));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getReason()).isEqualTo("Conta não pode ser excluida");

        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve atualizar um usuário")
    void updateUsuarioTest() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setUsername("novousuario@email.com");
        usuario.setNome("Novo Usuário");

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(id);
        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existingUsuario));
        // Mock do save para retornar o usuário atualizado
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioAtualizado = usuarioService.update(usuario, id);

        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));


        assertThat(usuarioAtualizado).isNotNull();
        assertThat(usuarioAtualizado.getId()).isEqualTo(id);
        assertThat(usuarioAtualizado.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioAtualizado.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(usuarioAtualizado.getPassword()).isEqualTo(existingUsuario.getPassword());
    }

    @Test
    @DisplayName("Deve lançar um erro quando o usuário não existir")
    void updateUsuarioNotFoundTest() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setUsername("novousuario@email.com");
        usuario.setNome("Novo Usuário");

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.update(usuario, id));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Não existe usuário com esse id " + id +".");
    }

    @Test
    @DisplayName("Deve lançar um erro quando o e-mail já está sendo usado por outro usuário")
    void updateUsuarioEmailConflictTest() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setUsername("novousuario@email.com");
        usuario.setNome("Novo Usuário");

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(id + 1);

        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existingUsuario));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.update(usuario, id));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getReason()).isEqualTo("O e-mail já está sendo usado por outro usuário");
    }



    @Test
    @DisplayName("Deve atualizar apenas o nome do usuário")
    void updateUsuarioNomeTest() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setNome("Novo Nome");

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(id);
        existingUsuario.setUsername("usuarioexistente");
        existingUsuario.setNome("Nome Antigo");

        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioAtualizado = usuarioService.update(usuario, id);

        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        assertThat(usuarioAtualizado).isNotNull();
        assertThat(usuarioAtualizado.getId()).isEqualTo(id);
        assertThat(usuarioAtualizado.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioAtualizado.getUsername()).isEqualTo(existingUsuario.getUsername());
        assertThat(usuarioAtualizado.getPassword()).isEqualTo(existingUsuario.getPassword());
    }


    @Test
    @DisplayName("Deve atualizar apenas o e-mail do usuário")
    void updateUsuarioEmailTest() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setUsername("novoemail@usuario.com");

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(id);
        existingUsuario.setUsername("usuarioexistente");
        existingUsuario.setNome("Nome Antigo");

        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioAtualizado = usuarioService.update(usuario, id);

        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        assertThat(usuarioAtualizado).isNotNull();
        assertThat(usuarioAtualizado.getId()).isEqualTo(id);
        assertThat(usuarioAtualizado.getNome()).isEqualTo(existingUsuario.getNome());
        assertThat(usuarioAtualizado.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(usuarioAtualizado.getPassword()).isEqualTo(existingUsuario.getPassword());
    }

    @Test
    @DisplayName("Deve atualizar o nome e o e-mail do usuário")
    void updateUsuarioNomeEmailTest() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setUsername("novoemail@usuario.com");
        usuario.setNome("Novo Nome");

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(id);
        existingUsuario.setUsername("usuarioexistente");
        existingUsuario.setNome("Nome Antigo");

        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioAtualizado = usuarioService.update(usuario, id);

        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        assertThat(usuarioAtualizado).isNotNull();
        assertThat(usuarioAtualizado.getId()).isEqualTo(id);
        assertThat(usuarioAtualizado.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioAtualizado.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(usuarioAtualizado.getPassword()).isEqualTo(existingUsuario.getPassword());
    }



    @Test
    @DisplayName("Deve alterar a senha do usuário com sucesso")
    void changePasswordSuccessTest() {
        String senhaAtual = "senhaAntiga+1";
        String novaSenha = "novaSenha+1";
        String confirmaNovaSenha = "novaSenha+1";
        String email = "usuario@teste.com";

        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setPassword(senhaAtual);

        Map<String, String> request = new HashMap<>();
        request.put("senhaAtual", senhaAtual);
        request.put("novaSenha", novaSenha);
        request.put("confirmaNovaSenha", confirmaNovaSenha);
        request.put("email", email);

        when(usuarioRepository.findByUsername(email)).thenReturn(Optional.of(usuario));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        usuarioService.changePassword(request);

        verify(usuarioRepository, times(2)).findByUsername(email);
        verify(encoder, times(1)).matches(eq(senhaAtual), anyString());
    }

    @Test
    @DisplayName("Deve lançar um erro quando a confirmação da nova senha não corresponder à nova senha")
    void changePasswordInvalidConfirmationTest() {
        String senhaAtual = "senhaAntiga";
        String novaSenha = "novaSenha";
        String confirmaNovaSenha = "senhaDiferente";
        String email = "usuario@teste.com";

        Map<String, String> request = new HashMap<>();
        request.put("senhaAtual", senhaAtual);
        request.put("novaSenha", novaSenha);
        request.put("confirmaNovaSenha", confirmaNovaSenha);
        request.put("email", email);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.changePassword(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo("A confirmação da nova senha não corresponde à nova senha");

        verify(usuarioRepository, never()).findByUsername(any());
        verify(encoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Deve lançar um erro quando a senha atual fornecida está incorreta")
    void changePasswordInvalidCurrentPasswordTest() {
        String senhaAtual = "senhaAntiga+1";
        String novaSenha = "novaSenha+1";
        String confirmaNovaSenha = "novaSenha+1";
        String email = "usuario@teste.com";

        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setPassword("senhaDiferente+_1");

        Map<String, String> request = new HashMap<>();
        request.put("senhaAtual", senhaAtual);
        request.put("novaSenha", novaSenha);
        request.put("confirmaNovaSenha", confirmaNovaSenha);
        request.put("email", email);

        when(usuarioRepository.findByUsername(email)).thenReturn(Optional.of(usuario));
        when(encoder.matches(senhaAtual, usuario.getPassword())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> usuarioService.changePassword(request));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("A senha atual fornecida está incorreta");

        verify(usuarioRepository, times(1)).findByUsername(email);
        verify(encoder, times(1)).matches(senhaAtual, usuario.getPassword());
    }

    @Test
    @DisplayName("Deve retornar verdadeiro para uma senha válida")
    void isValidPassword_ValidPassword_ReturnsTrue() {
        // Arrange
        String password = "Senha123!";

        // Act
        boolean result = usuarioService.isValidPassword(password);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve lançar exceção para uma senha sem letra maiúscula")
    void isValidPassword_NoUppercaseLetter_ThrowsException() {
        // Arrange
        String password = "senha123!";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usuarioService.isValidPassword(password));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha deve atender aos seguintes requisitos:\n" +
                "- Pelo menos uma letra maiúscula\n" +
                "- Pelo menos uma letra minúscula\n" +
                "- Pelo menos um número\n" +
                "- Pelo menos um dos seguintes caracteres especiais: !@#$%¨&*()_-+=§/?°´{[ª]}º|\\<>.,;", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção para uma senha sem letra minúscula")
    void isValidPassword_NoLowercaseLetter_ThrowsException() {
        // Arrange
        String password = "SENHA123!";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usuarioService.isValidPassword(password));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha deve atender aos seguintes requisitos:\n" +
                "- Pelo menos uma letra maiúscula\n" +
                "- Pelo menos uma letra minúscula\n" +
                "- Pelo menos um número\n" +
                "- Pelo menos um dos seguintes caracteres especiais: !@#$%¨&*()_-+=§/?°´{[ª]}º|\\<>.,;", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção para uma senha sem número")
    void isValidPassword_NoNumber_ThrowsException() {
        // Arrange
        String password = "SenhaFortE!";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usuarioService.isValidPassword(password));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha deve atender aos seguintes requisitos:\n" +
                "- Pelo menos uma letra maiúscula\n" +
                "- Pelo menos uma letra minúscula\n" +
                "- Pelo menos um número\n" +
                "- Pelo menos um dos seguintes caracteres especiais: !@#$%¨&*()_-+=§/?°´{[ª]}º|\\<>.,;", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção para uma senha sem caractere especial")
    void isValidPassword_NoSpecialCharacter_ThrowsException() {
        // Arrange
        String password = "SenhaFortE123";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usuarioService.isValidPassword(password));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha deve atender aos seguintes requisitos:\n" +
                "- Pelo menos uma letra maiúscula\n" +
                "- Pelo menos uma letra minúscula\n" +
                "- Pelo menos um número\n" +
                "- Pelo menos um dos seguintes caracteres especiais: !@#$%¨&*()_-+=§/?°´{[ª]}º|\\<>.,;", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção para uma senha com menos de 6 caracteres")
    void isValidPassword_TooShortPassword_ThrowsException() {
        // Arrange
        String password = "Pas1!";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usuarioService.isValidPassword(password));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha deve atender aos seguintes requisitos:\n" +
                "- Pelo menos uma letra maiúscula\n" +
                "- Pelo menos uma letra minúscula\n" +
                "- Pelo menos um número\n" +
                "- Pelo menos um dos seguintes caracteres especiais: !@#$%¨&*()_-+=§/?°´{[ª]}º|\\<>.,;", exception.getReason());
    }

    private void start() {
        usuario = new Usuario(null, "ana@email.com.br", "Ana Maria", "SenhaModifaca+-3");
    }

}
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { UsuarioService } from '../services/usuario.service';
import { Usuario } from '../login/usuario';
import { Message } from '@angular/compiler/src/i18n/i18n_ast';


@Component({
  selector: 'app-usuario',
  templateUrl: './usuario.component.html',
  styleUrls: ['./usuario.component.css']
})
export class UsuarioComponent implements OnInit {

  usuario: Usuario;
  mensagem: string;
  cor: string;
  username: string;
  editarForm: boolean = false;
  alterarSenhaForm: boolean = false;
  senhaAtual: string;
  novaSenha: string;
  confirmarNovaSenha: string;
  errors: string[] = [];

  constructor(private usuarioService: UsuarioService,
     private authService: AuthService,
     private router: Router
     ) { }

  ngOnInit(): void {  
    this.buscarUsuario();
  }

  buscarUsuario() {
    this.username = this.authService.getUserName();
    if (this.username) {
      this.usuarioService.usuarioByUsername(this.username).subscribe(
        (response) => {
          this.usuario = response;
        },
        (errorResponse) => {
          this.errors = [`${errorResponse.error.message}`];
        }
      )
    };
  }

  editarDados() {
    this.editarForm = true;
  }
  
  cancelarEdicao() {
    this.editarForm = false;
    this.mensagem = '';
    this.ngOnInit();
  }

  salvarDados() {
    this.mensagem = '';
    // Lógica para salvar os dados do usuário após a edição
    // Verifique se os campos obrigatórios foram preenchidos corretamente antes de prosseguir com o salvamento
    if (this.usuario.nome && this.usuario.username) {
      this.usuarioService.alterarUsuario(this.usuario.username, this.usuario.nome, this.usuario.id).subscribe(
        response => {

          this.editarForm = false;
        },
        error => {
          this.mensagem = error.error.erros;
          this.cor = 'danger';
        }
      );
    } else {
      this.cor = 'danger';
      this.mensagem = ("Preencha todos os campos obrigatórios.");
    }
  }
  
  abrirFormularioAlterarSenha(event: Event) {
    event.preventDefault();
    this.alterarSenhaForm = true;
  }
  

  cancelarAlteracaoSenha() {
    this.alterarSenhaForm = false;
    this.senhaAtual = '';
    this.novaSenha = '';
    this.confirmarNovaSenha = '';
    this.mensagem = '';
  }

  salvarNovaSenha() {

    if (!this.senhaAtual || !this.novaSenha || !this.confirmarNovaSenha) {
      this.mensagem = "Por favor, preencha todos os campos.";
      this.cor = "danger";
      return;
    }

    // Verificar se a nova senha e a confirmação são iguais
    if (this.novaSenha !== this.confirmarNovaSenha) {
      this.mensagem = ("A nova senha e a confirmação da senha não coincidem.");
      this.cor = "danger";
      return;
    }

    this.usuarioService.alterarSenha(this.senhaAtual,this.novaSenha, this.confirmarNovaSenha, 
      this.usuario.username).subscribe(
      response => {
        console.log(response);
            // Limpar os campos de senha
    this.senhaAtual = '';
    this.novaSenha = '';
    this.confirmarNovaSenha = '';
    this.mensagem ='';

    // Fechar o formulário de alteração de senha e exibir o card novamente
    this.alterarSenhaForm = false;
      },
      error => {
        console.log(error.error.erros)
        this.mensagem = error.error.erros;
        this.cor = 'danger';
      }
    );
  }

  
  confirmarExclusao() {
    this.usuarioService.deleteById(this.usuario.id).subscribe(
      response => {
        localStorage.removeItem('access_token');
        ($('#confirmModal') as any).modal('hide'); // Fechar o modal
        this.router.navigate(['/login']);
      },
      error => {
        this.mensagem = error.error.erros;
        this.cor = 'danger';
      }
    );
  }
  excluirConta() {
    ($('#confirmModal') as any).modal('show');
  }
  cancelarExclusao() {
    ($('#confirmModal') as any).modal('hide');
  }
}
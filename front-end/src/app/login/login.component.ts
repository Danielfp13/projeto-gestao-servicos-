import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Usuario } from './usuario';
import { EmailService } from '../services/email-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  usuario: Usuario;
  username: string;
  password: string;
  loginError: boolean;
  cadastrando: boolean;
  esqueceuSenha: boolean = false;
  mensagem: string;
  errors: string[] = [];

  constructor(
    private router: Router,
    private service: AuthService,
    private emailService: EmailService

  ) {
    this.usuario = new Usuario();
  }

  inserirUsuario() {
    this.service.insertUsuario(this.usuario).subscribe({
      next: (response) => {
        this.mensagem = 'Cadastro realizado com sucesso! Efetue o login.'
        this.errors = [];
        this.cadastrando = false;
        this.usuario = new Usuario();
      },
      error: (responseError) => {
        console.log(responseError.error.errons)
        this.errors = responseError.error.erros;
        this.mensagem = "";
      }
    })
  }

  forgotPassword() {
    const urlFront = window.location.href.split('/login')[0]; // Obter a parte da URL antes de "/login"

    this.emailService.enviarEmail(this.usuario.username, urlFront).subscribe({
      next: (response) => {
        this.mensagem = "Um e-mail foi enviado com sucesso para o endereço fornecido. Por favor, verifique sua caixa de entrada.";
        this.errors = [];
        this.usuario = new Usuario();
        setTimeout(() => {
          this.cadastrando = false;
          this.esqueceuSenha = false;
          this.mensagem = "";
        }, 3000)
      },
      error: (responseError) => {
        console.log(responseError.error.erros)
        this.errors = [responseError.error.erros];
      }
    })
  }

  onSubmit() {
    this.service.tentarLogar(this.usuario).subscribe({
      next: (response) => {
        const access_token = JSON.stringify(response);
        localStorage.setItem('access_token', access_token);
        this.router.navigate(['/home']);
      },
      error: (responseError) => {
        this.errors = [];
        this.mensagem = ""; 
        this.errors = ['Usuarios e ou senha incorretos!'];
      }
    })
  }

  preparaCadastrar(event: any) {
    event.preventDefault();
    this.cadastrando = true;
    this.esqueceuSenha = false;
    this.mensagem = "";
    this.errors = [];
    this.resetarCampos();
  }

  preparaEsqueceuSenha(event: any) {
    event.preventDefault();
    this.esqueceuSenha = true;
    this.cadastrando = false;
    this.mensagem = "";
    this.errors = [];
    this.resetarCampos();
  }

  cancelaCadastro() {
    this.cadastrando = false;
    this.esqueceuSenha = false;
    this.errors = [];
    this.mensagem = "";
    this.resetarCampos();
  }

resetarCampos(){
  this.usuario.nome = '';
  this.usuario.username = '';
  this.usuario.password = '';
  
}
}

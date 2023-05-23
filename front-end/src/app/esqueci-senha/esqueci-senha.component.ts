import { Component } from '@angular/core';
import { EmailService } from '../services/email-service.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-esqueci-senha',
  templateUrl: './esqueci-senha.component.html',
  styleUrls: ['./esqueci-senha.component.css']
})
export class EsqueciSenhaComponent {

  token: string;
  newPassword: string;
  confirmPassword: string;
  welcomeMessage: string;
  mensagem: string;
  corMensagem: string = "danger";

  constructor(private route: ActivatedRoute, private resetPasswordService: EmailService, private router: Router,) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      this.welcomeMessage = this.resetPasswordService.getWelcomeMessage(this.token);
    });
  }

  resetPassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.mensagem="As senhas não correspondem. Por favor, tente novamente.";
      return;
    }
    this.resetPasswordService.resetPassword(this.token, this.newPassword).subscribe(
      response => {
        this.corMensagem = 'success';
        this.mensagem = "Senha alterada com sucesso.";
        setTimeout( ()=>{
          this.router.navigate(['/home']);
        },7000)
      },
      error => {
        this.mensagem = "Desculpe, este link de recuperação de senha não é mais válido.\nO token expirou ou o link não existe mais.\nPor favor, solicite uma nova recuperação de senha.";
        this.corMensagem = 'danger';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 9000);
      }
    );
  }

}

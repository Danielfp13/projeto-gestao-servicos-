import { UsuarioService } from './../../services/usuario.service';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserUpdateService } from 'src/app/services/userupdate.service';
import { Subscription } from 'rxjs';
import { Usuario } from 'src/app/login/usuario';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy {

  usuario: Usuario;
  usuarioLogado: string;
  username: string;
  roles: string[];
  private userUpdateSubscription: Subscription;

  constructor(private authService: AuthService,
    private router: Router,
    private userUpdateService: UserUpdateService,
    private usuarioService: UsuarioService
  ) { }



  ngOnInit(): void {
    this.carregarDados();
    this.userUpdateSubscription = this.userUpdateService.userUpdated$.subscribe(() => {
      this.carregarDados();
    });
  }

  carregarDados() {
    this.username = this.authService.getUserName();
    this.usuarioService.usuarioByUsername(this.username).subscribe({
      next: (response) => {
        this.usuario = response;
        this.usuarioLogado = this.usuario.nome;
        this.roles = this.usuario.perfis;
      }
    });
  }

  ngOnDestroy(): void {
    this.userUpdateSubscription.unsubscribe();
  }

  logout() {
    this.authService.enserrarSessao();
    this.router.navigate(['/login']);
  }

}

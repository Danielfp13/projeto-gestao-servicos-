import { Usuario } from 'src/app/login/usuario';
import { Component, OnInit } from '@angular/core';
import { PaginaUsuario } from '../usuario-form/pagina-usuario';
import { UsuarioService } from 'src/app/services/usuario.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-usuario-list',
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.css']
})
export class UsuarioListComponent implements OnInit {

  usuarios: Usuario[] = [];
  usuarioSelecionado: Usuario;
  menssagemDeResposta: string;
  cor: string = 'denger';
  roles: string[];

  page: number = 0;
  linePerPage: number = 3;
  direction: string = 'ASC';
  orderBy: string = 'id';
  totalElementos: number = 0;
  totalPage: number = 0;

  pageSizeOptions: number[] = [3, 6, 9];

  paginaUsuario: PaginaUsuario;

  constructor(private service: UsuarioService,
    private router: Router, private authService: AuthService) {

  }

  ngOnInit(): void {
    this.roles = this.authService.getRole();
    this.findPage(this.page, this.linePerPage, this.direction, this.orderBy);
  }

  addPerfilAdmin(id: number) {
    this.service.addPerfilAdmin(id).subscribe({
      next: (response) => {
        this.showMessage('Perfil Admin adicionado com sucesso.', 'success');
      },
      error: (responseError) => {
        this.showMessage(responseError.error.erros, 'danger');
      }
    })
  }


  removePerfilAdmin(id: number) {
    this.service.removeAdmin(id).subscribe({
      next: (response) => {
        this.showMessage('Perfil Admin foi retirado com sucesso.', 'success');
      },
      error: (responseError) => {
        this.showMessage(responseError.error.erros, 'danger');
      }
    })

  }

  findPage(page: number, linePerPage: number, direction: string, orderBy: string) {
    this.menssagemDeResposta = '';
    this.service.buscarPagina(page, linePerPage, direction, orderBy).subscribe({
      next: (resposta) => {
        this.usuarios = resposta.content;
        this.totalElementos = resposta.totalElements
        this.totalPage = resposta.totalPages
        this.page = resposta.number
      }
    })
  }

  paginar(event: PageEvent) {
    this.page = event.pageIndex
    this.linePerPage = event.pageSize
    this.findPage(this.page, this.linePerPage, this.direction, this.orderBy);
  }


  preparaDelecao(usuario: Usuario) {
    this.usuarioSelecionado = usuario;
  }

  deletar() {
    this.service.deleteById(this.usuarioSelecionado.id).subscribe({
      next: (response) => {
        this.menssagemDeResposta = `Cliente  ${this.usuarioSelecionado.nome} excluido com sucesso.`
        this.cor = 'success';
        this.ngOnInit();
      },
      error: (errorResponse) => {
        this.menssagemDeResposta = errorResponse.error.erros;
        this.cor = 'danger';
      }
    })
  }

  showMessage(message: string, color: string) {
    this.ngOnInit();
    this.menssagemDeResposta = message;
    this.cor = color;

    setTimeout(() => {
      this.clearMessage();
    }, 3000);
  }

  clearMessage() {
    this.menssagemDeResposta = '';
    this.cor = '';
  }
}
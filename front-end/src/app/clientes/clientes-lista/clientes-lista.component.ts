import { PageEvent } from '@angular/material/paginator';
import { PaginaCliente } from './pagina-clientes';
import { Component, OnInit } from '@angular/core';
import { Cliente } from '../Cliente';
import { Router } from '@angular/router';
import { ClientesService } from '../../services/clientes.service';
import { AuthService } from 'src/app/services/auth.service';



@Component({
  selector: 'app-clientes-lista',
  templateUrl: './clientes-lista.component.html',
  styleUrls: ['./clientes-lista.component.css']
})
export class ClientesListaComponent implements OnInit {

  clientes: Cliente[] = [];
  clienteSelecionado: Cliente;
  menssageDeResposta: string;
  cor: string = 'denger';
  roles: string[];

  page: number = 0;
  linePerPage: number = 3;
  direction: string = 'ASC';
  orderBy: string = 'id';
  totalElementos: number = 0;
  totalPage: number = 0;

  pageSizeOptions: number[] = [3, 6, 9];

  paginaCliente: PaginaCliente;

  constructor(private service: ClientesService,
    private router: Router, private authService: AuthService) {

  }

  ngOnInit(): void {
    this.roles = this.authService.getRole();
    console.log(this.roles)
    this.findPage(this.page, this.linePerPage, this.direction, this.orderBy);
  }

  findPage(page: number, linePerPage: number, direction: string, orderBy: string){
    this.service.buscarPagina(page, linePerPage, direction, orderBy).subscribe({
      next: (resposta) => {
      this.clientes = resposta.content;
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
  novoCadastro() {
    this.router.navigate(['/clientes/form'])
  }

  preparaDelecao(cliente: Cliente) {
    this.clienteSelecionado = cliente;
  }

  deletar() {
    this.service.deleteById(this.clienteSelecionado.id).subscribe({
      next: (response) => {
        this.menssageDeResposta = `Cliente  ${this.clienteSelecionado.nome} excluido com sucesso.`
        this.cor = 'success';
        this.ngOnInit();
      },
      error: (errorResponse) => {
        this.menssageDeResposta = errorResponse.error.erros;
        this.cor = 'danger';
      }
    })
  }
}
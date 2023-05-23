import { AuthService } from 'src/app/services/auth.service';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ServicoPrestadoBusca } from './servico-prestado-busca';
import { ServicoPrestadoService } from '../../services/servico-prestado.service';
import { PaginaServicoPrestado } from '../paginaservicoprestado';
import { MesesAno } from './mesesAno';


@Component({
  selector: 'app-servico-prestado-lista',
  templateUrl: './servico-prestado-lista.component.html',
  styleUrls: ['./servico-prestado-lista.component.css']
})
export class ServicoPrestadoListaComponent implements OnInit, AfterViewInit {

  nome: string = "";
  mes: number;
  servicosPrestados: ServicoPrestadoBusca[] = [];
  message: string;
  roles: string[];
  page: PaginaServicoPrestado;
  paginaAtual: number = 1;
  linhasPorPagina: number = 3;
  opcoesPorPagina: number[] = [3, 5, 10, 20, 50];
  totalItens: number = 0;
  timeoutConsulta: any;
  meses: MesesAno[] = [
    new MesesAno("todos", ""),
    new MesesAno("Janeiro", "01"),
    new MesesAno("Fevereiro", "02"),
    new MesesAno("Março", "03"),
    new MesesAno("Abril", "04"),
    new MesesAno("Maio", "05"),
    new MesesAno("Junho", "06"),
    new MesesAno("Julho", "07"),
    new MesesAno("Agosto", "08"),
    new MesesAno("Setembro", "09"),
    new MesesAno("Outubro", "10"),
    new MesesAno("Novembro", "11"),
    new MesesAno("Dezembro", "12")

  ]
  ordenarPor: string[] = ['maior valor', 'menor valor', 'recente', 'antigo', 'cliente a-z', 'cliente z-a']
  ordenar: string = '';
  orderBy: string = '';
  direction: string = '';

  constructor(private service: ServicoPrestadoService,
    private authService: AuthService) {

  }
  ngOnInit(): void {
    this.roles = this.authService.getRole();
    this.consultar();
    this.paginaAtual = 1;
  }

  ngAfterViewInit(): void {

  }

  consultar() {
    this.service.buscarPagina(this.nome, this.mes, this.paginaAtual, this.linhasPorPagina, this.direction, this.orderBy).subscribe({
      next: (response: any) => {
        this.servicosPrestados = response.content;
        this.totalItens = response.totalElements;
        this.page = response;
        if (this.servicosPrestados.length <= 0) {
          this.message = 'Nenhum serviço encontrado.'
        } else {
          this.message = "";
        }
      },
      error: (error: any) => {
      }
    });
  }

  onPaginaChange() {
    clearTimeout(this.timeoutConsulta);
    this.timeoutConsulta = setTimeout(() => {
      this.paginaAtual = 1
      this.consultar();
    }, 600);
  }

  onPageChange(event: any): void {
    this.paginaAtual = event;
    this.consultar();
  }

  ordenandoChange() {
    if (this.ordenar === 'maior valor') {
      this.direction = 'DESC'
      this.orderBy = 'valor'
      this.consultar();
    } else if (this.ordenar === 'menor valor') {
      this.orderBy = 'valor'
      this.direction = 'ASC'
      this.consultar();
    }else if (this.ordenar === 'recente') {
      this.orderBy = 'data'
      this.direction = 'DESC'
      this.consultar();
    }else if (this.ordenar === 'antigo') {
      this.orderBy = 'data'
      this.direction = 'ASC'
      this.consultar();
    }else if (this.ordenar === 'cliente a-z') {
      this.orderBy = 'cliente.nome'
      this.direction = 'ASC'
      this.consultar();
    }else if (this.ordenar === 'cliente z-a') {
      this.orderBy = 'cliente.nome'
      this.direction = 'DESC'
      this.consultar();
    }

  }

  formatCurrency(value: number): string {
    return "R$ " + value.toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }
}
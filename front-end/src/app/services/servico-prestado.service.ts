import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ServicoPrestado } from '../servico-prestado/servicoprestado';
import { ServicoPrestadoBusca } from '../servico-prestado/servico-prestado-lista/servico-prestado-busca';
import { PaginaServicoPrestado } from '../servico-prestado/paginaservicoprestado';


@Injectable({
  providedIn: 'root'
})
export class ServicoPrestadoService {

  constructor(private http: HttpClient) { }

    apiURL: string = environment.apiURLBase;

    insertServicoPrestado(servicoPrestado: ServicoPrestado) :Observable<ServicoPrestado>{
    return this.http.post<ServicoPrestado>(`${this.apiURL}/servicos-prestado`,servicoPrestado);
    }

    buscar(nome: string, mes: number): Observable<ServicoPrestadoBusca[]>{

      const httpParams = new HttpParams().set("nome", nome).set("mes", mes ? mes.toString(): ''); 
      const url = this.apiURL + '/servicos-prestado/search?' + httpParams.toString();
      return this.http.get<ServicoPrestadoBusca[]>(url)
    }

    buscarPagina(nome: string, mes: number, currentPage: number, pageSize: number, direction: string, orderBy: string): Observable<PaginaServicoPrestado>{
      const httpParams = new HttpParams().set("nome", nome).set("mes", mes ? mes.toString(): '')
      .set("page", currentPage -1).set("size", pageSize).set("direction", direction).set("orderBy",orderBy);
      const url = this.apiURL + '/servicos-prestado/search?' + httpParams.toString();
      return this.http.get<any>(url)
    }
  

}
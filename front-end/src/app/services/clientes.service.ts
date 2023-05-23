import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Cliente } from '../clientes/Cliente';
import { PaginaCliente } from '../clientes/clientes-lista/pagina-clientes';

@Injectable({
  providedIn: 'root',
})
export class ClientesService {
  constructor(private http: HttpClient) {}

  apiURL: string = environment.apiURLBase;

  salvar(cliente: Cliente): Observable<Cliente> {

    return this.http.post<Cliente>(`${this.apiURL}/clientes`,cliente);
  }

  listaCliente(): Observable<Cliente[]> {

    return this.http.get<Cliente[]>(`${this.apiURL}/clientes`);
  }

  buscarPagina(page: number, linePerPage: number, direction: string, orderBy: string): Observable<PaginaCliente>{
    const httpParams = new HttpParams()
    .set("page", page)
    .set("linePerPage", linePerPage)
    .set("direction", direction)
    .set("orderBy", orderBy);
    const url = this.apiURL + '/clientes/page?' + httpParams.toString();
    return this.http.get<any>(url)
  }


  alterarCliente(cliente: Cliente, id: any):Observable<any>{
    return this.http.put<Cliente>(`${this.apiURL}/clientes/${id}`,cliente);
  }

  getClienteById(id: any): Observable<Cliente>{
    return this.http.get<Cliente>(`${this.apiURL}/clientes/${id}`);
  }

  deleteById(id: any) : Observable<any>{
    return this.http.delete<any>(`${this.apiURL}/clientes/${id}`);
  }
}

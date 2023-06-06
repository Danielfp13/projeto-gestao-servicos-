import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Usuario } from '../login/usuario';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PaginaUsuario } from '../usuario/pagina-usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private http: HttpClient) { }

  apiURL: string = environment.apiURLBase;


  alterarUsuario(username: string, nome: string, id: number): Observable<any> {
    const body = { username: username, nome: nome};
    return this.http.put<Usuario>(`${this.apiURL}/usuarios/${id}`, body);
  }

  alterarSenha(senhaAtual: string, novaSenha: string,
     confirmaNovaSenha: string, email: string ): Observable<any> {
      console.log("metodo certo")
    const body = { senhaAtual: senhaAtual, novaSenha: novaSenha,
       confirmaNovaSenha: confirmaNovaSenha, email: email};
    return this.http.post<Usuario>(`${this.apiURL}/usuarios/alterar-senha`, body);
  }

  usuarioByUsername(username: string): Observable<Usuario> {
    console.log(username)
    return this.http.get<Usuario>(`${this.apiURL}/usuarios/username/${username}`);
  }

  deleteById(id: any): Observable<any> {
    return this.http.delete<any>(`${this.apiURL}/usuarios/${id}`);
  }

  buscarPagina(page: number, linePerPage: number, direction: string, orderBy: string): Observable<PaginaUsuario> {
    const httpParams = new HttpParams()
      .set("page", page)
      .set("linePerPage", linePerPage)
      .set("direction", direction)
      .set("orderBy", orderBy);
    const url = this.apiURL + '/usuarios/page?' + httpParams.toString();
    return this.http.get<any>(url)
  }

}
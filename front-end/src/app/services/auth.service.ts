import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { Usuario } from '../login/usuario';
import { environment } from '../../environments/environment';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  urlApi: string = environment.apiURLBase + '/usuarios';
  tokenURL: string = environment.apiURLBase + environment.obterTokenUrl;
  urlBasic: string = environment.apiURLBase;
  jwtHelper: JwtHelperService = new JwtHelperService();


  constructor(private http: HttpClient) { }

  insertUsuario(usuario: Usuario): Observable<any> {
    return this.http.post<Usuario>(`${this.urlApi}`, usuario);
  }

  tentarLogar(usuario: Usuario): Observable<any> {
    return this.http.post(`${this.tokenURL}`, usuario);
  }


  refreshToken(): Observable<any> {
    let tokenString = localStorage.getItem('access_token');
    if (!tokenString) {
      // Trate aqui o caso em que não há token armazenado
      return throwError('Não há token armazenado');
    }
    const userName = JSON.parse(tokenString).userName;
    const headers = {
      Authorization: `${this.obterRefleshToken()}`
    };
    return this.http.put<any>(`${this.urlBasic}/auth/refresh/${userName}`, {}, { headers });
  }

  obterToken() {
    let tokenString = localStorage.getItem('access_token');
    if (tokenString) {
      const token = JSON.parse(tokenString).accesToken;
      return token;
    }
    return null;
  }

  obterRefleshToken() {
    let tokenString = localStorage.getItem('access_token');
    if (tokenString) {
      const refleshToken = JSON.parse(tokenString).refreshToken;
      return refleshToken;
    }
    return null;
  }

  isAutheticated(): boolean {
    const token = this.obterToken();
    if (token) {
      const expired = this.jwtHelper.isTokenExpired(token);
      if (expired) {
        const expirationDate = this.jwtHelper.getTokenExpirationDate(token);
        const currentDate = new Date();
        const timeDiff = (currentDate.getTime() - (expirationDate ?? currentDate).getTime()) / 1000; // Diferença de tempo em segundos
        const minutosInativos: number = 5;
 
        
        if (timeDiff > minutosInativos * 60) {
          localStorage.removeItem("access_token");
          return false; // Retorna false apenas se a diferença de tempo for maior que 5 minutos (minutosInativos * 60 segundos)
        }
      }
      return true; // Se o token não estiver expirado ou se a diferença de tempo for maior que 5 minutos, retorna true
    }
    return false; // Se não houver token, retorna false
  }
  
  

  enserrarSessao() {
    localStorage.removeItem('access_token');
  }

  getUsuarioAutenticado() {
    let indice = 0;
    const token = this.obterToken();
    const usuario = this.jwtHelper.decodeToken(token).usuario;
    return usuario;
  }

  getUserName() {
    let tokenString = localStorage.getItem('access_token');
    if (tokenString) {
      const refleshToken = JSON.parse(tokenString).userName;
      return refleshToken;
    }
    return null;
  }


  getRole() {
    const token = this.obterToken();
    if (token) {
      const role = this.jwtHelper.decodeToken(token).perfils;
      return role;
    }
  }
}

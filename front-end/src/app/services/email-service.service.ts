import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class EmailService {

  private apiUrl = `${environment.apiURLBase}/api/password`; 
  jwtHelper: JwtHelperService = new JwtHelperService();


  constructor(private http: HttpClient) { }

  enviarEmail(username: string, urlFront: string) {
    const body = { email: username, urlFront: urlFront};
    console.log(urlFront);
    return this.http.post<any>(`${this.apiUrl}/forgot`, body);
  }

  public getWelcomeMessage(token: string): string {
    const decodedToken = this.jwtHelper.decodeToken(token);
    const userName = decodedToken.sub;
    return `Bem-vindo, ${userName}! Aqui você pode redefinir sua senha.`;
  }

  resetPassword(token: string, password: string): Observable<any> {
    const body = {
      token: token,
      password: password
    };
    return this.http.post<any>(`${this.apiUrl}/reset`, body);
  }
}

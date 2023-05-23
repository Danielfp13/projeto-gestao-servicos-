

import { AuthService } from 'src/app/services/auth.service';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  private isRefreshing = false;

  constructor(private authService: AuthService) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    let tokenString = localStorage.getItem('access_token');
    const url = request.url;

    if (tokenString && !url.endsWith('/auth/signin') && !url.includes(`/auth/refresh/`)) {
      let token = JSON.parse(tokenString);
      let expiration = token.expiration;

      if (new Date(expiration) < new Date() && !this.isRefreshing) {
        // Define isRefreshing como true para evitar várias solicitações de atualização do token
        this.isRefreshing = true;
        // Chama o método refreshToken() para obter um novo token
        return this.authService.refreshToken().pipe(
          switchMap((response: any) => {
            const access_token = JSON.stringify(response);
            localStorage.setItem('access_token', access_token);
            this.isRefreshing = false;
            // Clona a solicitação original e adiciona o novo token ao cabeçalho
            request = request.clone({
              setHeaders: {
                'Authorization': 'Bearer ' + response.accesToken
              }
            });
            // Envie a nova solicitação com o novo token
            return next.handle(request);
          }),
          catchError((error: any) => {
            // Trata o erro e o repassa ao chamador
            this.isRefreshing = false;
            return throwError(error);
          })
        );
      }
      // Se o token não estiver expirado ou já estiver sendo atualizado, adicione o token ao cabeçalho
      tokenString = localStorage.getItem('access_token');
      if (tokenString !== null) {
        token = JSON.parse(tokenString);
        const jwt = token.accesToken;
        request = request.clone({
          setHeaders: {
            'Authorization': 'Bearer ' + jwt
          }
        })
      }
    }
    // Se o token não estiver presente, envie a solicitação original
    return next.handle(request);
  }
}



/* import { AuthService } from 'src/app/services/auth.service';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  constructor(private authService: AuthService) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    let tokenString = localStorage.getItem('access_token');
    const url = request.url;
    
    if (tokenString && !url.endsWith('/auth/signin') && !url.includes(`/auth/refresh/`)) {
      let token = JSON.parse(tokenString);
      let expiration = token.expiration;

      if (new Date(expiration) < new Date() && !this.isRefreshing) {
        this.isRefreshing = true;
        this.authService.refreshToken().subscribe({
          next: (response: any) => {
            const access_token = JSON.stringify(response);
            localStorage.setItem('access_token', access_token);
            this.isRefreshing = false;
          },
          error: (error: any) => {
            this.isRefreshing = false;
          }
        })
      }
      tokenString = localStorage.getItem('access_token') ;
      if (tokenString !== null) {
        token = JSON.parse(tokenString);
       let jwt = token.accesToken;
        expiration = token.expiration;
        request = request.clone({
          setHeaders: {
            'Authorization': 'Bearer ' + jwt
          }
        })
      }
    }
    return next.handle(request);
  }
}
 */
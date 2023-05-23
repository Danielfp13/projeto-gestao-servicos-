import { AuthService } from '../services/auth.service';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private service: AuthService, private router: Router){

  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    const authenticated = this.service.isAutheticated();
    console.log("Usuario autenticado" + authenticated)
    if(authenticated){
      return true;
    }else{
      this.router.navigate(['/login'])
    }
    return false;
  }
  
}
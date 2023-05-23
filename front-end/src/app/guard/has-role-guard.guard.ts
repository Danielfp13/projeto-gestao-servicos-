import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';


@Injectable({
  providedIn: 'root'
})
export class HasRoleGuard implements CanActivate {

  constructor(private service: AuthService, private router: Router) {}
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean  {
    const isAuthorized = this.service.getRole()?.includes(route.data['role']);
    console.log(" can ative" +this.service.getRole());
    if (!isAuthorized) {
      this.router.navigate(["/home"]);
    }
    return isAuthorized || false;
  }
}

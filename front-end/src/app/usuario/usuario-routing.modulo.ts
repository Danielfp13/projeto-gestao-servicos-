import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'src/app/guard/auth.guard';
import { LayoutComponent } from 'src/app/layout/layout.component';
import { HasRoleGuard } from 'src/app/guard/has-role-guard.guard';
import { UsuarioComponent } from './usuario-form/usuario.component';
import { UsuarioListComponent } from './usuario-list/usuario-list.component';



const routes: Routes = [

  {
    path: 'usuarios', component: LayoutComponent,
    canActivate: [AuthGuard], children: [
      {
        path: 'form', component: UsuarioComponent, canActivate: [AuthGuard, HasRoleGuard],
        data: {
          role: 'ROLE_USER'
        },
      },
      {
        path: 'lista', component: UsuarioListComponent, canActivate: [AuthGuard, HasRoleGuard],
        data: {
          role: 'ROLE_ADMIN'
        },
      },
      { path: '', redirectTo: '/usuarios/form', pathMatch: 'full' }
    ]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsuarioRoutingModule { }
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'src/app/guard/auth.guard';
import { LayoutComponent } from 'src/app/layout/layout.component';
import { UsuarioComponent } from '../usuario.component';
import { HasRoleGuard } from 'src/app/guard/has-role-guard.guard';



const routes: Routes = [

  {
    path: 'usuarios', component: LayoutComponent,
    canActivate: [AuthGuard], children: [
      {
        path: 'form/:id', component: UsuarioComponent, canActivate: [AuthGuard, HasRoleGuard],
        data: {
          role: 'ROLE_USER'
        }
      },
      {
        path: 'form', component: UsuarioComponent, canActivate: [AuthGuard, HasRoleGuard],
        data: {
          role: 'ROLE_USER'
        },
      },

    ]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsuarioRoutingModule { }
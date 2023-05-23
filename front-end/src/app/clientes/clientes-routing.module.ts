import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../guard/auth.guard';

import { LayoutComponent } from '../layout/layout.component';
import { ClientesFormComponent } from './clientes-form/clientes-form.component';
import { ClientesListaComponent } from './clientes-lista/clientes-lista.component';
import { HasRoleGuard } from '../guard/has-role-guard.guard';


const routes: Routes = [

  {
    path: 'clientes', component: LayoutComponent,
    canActivate: [AuthGuard], children: [
      {
        path: 'form/:id', component: ClientesFormComponent, canActivate: [AuthGuard, HasRoleGuard],
        data: {
          role: 'ROLE_ADMIN'
        }
      },
      {
        path: 'form', component: ClientesFormComponent, canActivate: [AuthGuard, HasRoleGuard],
        data: {
          role: 'ROLE_ADMIN',
        },
      },
      { path: 'lista', component: ClientesListaComponent },
      { path: '', redirectTo: '/clientes/lista', pathMatch: 'full' }

    ]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientesRoutingModule { }
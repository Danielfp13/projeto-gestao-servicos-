import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';
import { UsuarioRoutingModule } from './usuario-routing.modulo';
import { MatDialogModule } from '@angular/material/dialog';
import { UsuarioComponent } from './usuario-form/usuario.component';
import { UsuarioListComponent } from './usuario-list/usuario-list.component';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { PtBrMatPaginatorIntl } from '../clientes/clientes-lista/traducao-table';



@NgModule({
  declarations: [UsuarioComponent, UsuarioListComponent],
  providers: [
    {
      provide: MatPaginatorIntl,
      useClass: PtBrMatPaginatorIntl
      },
    // outros provedores necessários
  ],
  imports: [
    CommonModule,
    FormsModule,
    UsuarioRoutingModule,
    MatDialogModule,
    MatPaginatorModule,

  ],
  exports:
  [
    UsuarioComponent,
    UsuarioListComponent
  ]
})
export class UsuarioModule { }
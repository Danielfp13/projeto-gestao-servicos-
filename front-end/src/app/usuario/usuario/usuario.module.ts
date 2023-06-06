import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioComponent } from '../usuario.component';
import { FormsModule } from '@angular/forms';
import { UsuarioRoutingModule } from './usuario-routing.modulo';
import { MatDialogModule } from '@angular/material/dialog';



@NgModule({
  declarations: [UsuarioComponent],
  imports: [
    CommonModule,
    FormsModule,
    UsuarioRoutingModule,
    MatDialogModule

  ],
  exports:
  [
    UsuarioComponent,
  ]
})
export class UsuarioModule { }
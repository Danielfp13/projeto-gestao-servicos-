import { NgxPaginationModule } from 'ngx-pagination';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ServicoPrestadoRoutingModule } from './servico-prestado-routing.module';
import { ServicoPrestadoFormComponent } from './servico-prestado-form/servico-prestado-form.component';
import { ServicoPrestadoListaComponent } from './servico-prestado-lista/servico-prestado-lista.component';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MoedaDirective } from '../diretivas/moeda/moedaDiretiva';


@NgModule({
  declarations: [
    ServicoPrestadoFormComponent, 
    ServicoPrestadoListaComponent,
    MoedaDirective,
  ],
  imports: [
    CommonModule,
    ServicoPrestadoRoutingModule,
    FormsModule,
    RouterModule,
    NgxPaginationModule
    
  ], exports:[
    ServicoPrestadoFormComponent,
    ServicoPrestadoListaComponent
  ]
})
export class ServicoPrestadoModule { }

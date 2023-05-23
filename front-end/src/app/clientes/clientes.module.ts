import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientesRoutingModule } from './clientes-routing.module';
import { ClientesFormComponent } from './clientes-form/clientes-form.component';
import { FormsModule } from '@angular/forms';
import { ClientesListaComponent } from './clientes-lista/clientes-lista.component';
import { CpfMaskDirective } from '../diretivas/cpf/cpfDiretiva';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { PtBrMatPaginatorIntl } from './clientes-lista/traducao-table';


@NgModule({
  declarations: [
    ClientesFormComponent,
    ClientesListaComponent,
    CpfMaskDirective
  ],
  providers: [
    {
      provide: MatPaginatorIntl,
      useClass: PtBrMatPaginatorIntl
      },
    // outros provedores necessários
  ],
  imports:
   [
    CommonModule,
    ClientesRoutingModule,
    FormsModule,
    MatPaginatorModule,
  ],
  exports:
   [ClientesFormComponent,ClientesListaComponent],
})
export class ClientesModule {}
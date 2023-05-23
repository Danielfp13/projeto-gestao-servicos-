import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { TemplateModule } from './template/template.module';
import { HomeComponent } from './home/home.component'
import { ClientesModule } from './clientes/clientes.module';
import { ClientesService } from './services/clientes.service';
import { ServicoPrestadoModule } from './servico-prestado/servico-prestado.module';
import { ServicoPrestadoService } from './services/servico-prestado.service';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { LayoutComponent } from './layout/layout.component';
import { AuthService } from './services/auth.service';
import { TokenInterceptor } from './interceptor/token.interceptor';
import { NgxPaginationModule } from 'ngx-pagination';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { PtBrMatPaginatorIntl } from './clientes/clientes-lista/traducao-table';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EsqueciSenhaComponent } from './esqueci-senha/esqueci-senha.component';



@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    LayoutComponent,
    EsqueciSenhaComponent
    
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    TemplateModule,
    ClientesModule,
    ServicoPrestadoModule,
    FormsModule,
    NgxPaginationModule,  
    MatPaginatorModule,
    MatFormFieldModule,
    //BrowserAnimationsModule 

  ],
  providers: [
    ClientesService,
    ServicoPrestadoService,
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
    provide: MatPaginatorIntl,
    useClass: PtBrMatPaginatorIntl
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

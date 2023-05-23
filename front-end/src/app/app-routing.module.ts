import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { LayoutComponent } from './layout/layout.component';
import { AuthGuard } from './guard/auth.guard';
import { EsqueciSenhaComponent } from './esqueci-senha/esqueci-senha.component';


const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "forgot", component: EsqueciSenhaComponent },
  { path: '', component: LayoutComponent, canActivate: [ AuthGuard ], children: [
      { path: "home", component: HomeComponent },
      { path: '', redirectTo: '/home', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
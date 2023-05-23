import { RouterModule } from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './sidebar/sidebar.component';




@NgModule({
  declarations: [
    NavbarComponent,
    SidebarComponent
  ],
  imports: [
    CommonModule,
    RouterModule

  ],
  exports: [
    NavbarComponent,
    SidebarComponent
  ]
})
export class TemplateModule { }
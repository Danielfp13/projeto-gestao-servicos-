import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[appCpfMask]'
})
export class CpfMaskDirective {

  constructor() { }

  @HostListener('input', ['$event'])
  onInputChange(event: any) {
    // Remove qualquer caractere que não seja número
    let cpf = event.target.value.replace(/\D/g, '');
    
    // Adiciona pontos e traço no formato de CPF (XXX.XXX.XXX-XX)
    cpf = cpf.replace(/^(\d{3})(\d)/g, '$1.$2');
    cpf = cpf.replace(/^(\d{3})\.(\d{3})(\d)/g, '$1.$2.$3');
    cpf = cpf.replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d{1,2})/g, '$1.$2.$3-$4');
    
    // Define o valor formatado no campo de entrada
    event.target.value = cpf;
  }
}

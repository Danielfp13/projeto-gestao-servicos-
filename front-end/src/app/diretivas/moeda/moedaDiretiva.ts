import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appMoeda]'
})
export class MoedaDirective {

  constructor(private el: ElementRef) { }

  @HostListener('input')
  onInput() {
    let valor: string = this.el.nativeElement.value;
    valor = valor.replace(/\D/g, ''); // remove tudo o que não é dígito
    valor = (Number(valor) / 100).toFixed(2); // divide por 100 e fixa a quantidade de casas decimais
    valor = valor.replace('.', ','); // substitui o ponto pelo separador decimal
    valor = 'R$ ' + valor.replace(/(\d)(?=(\d{3})+\,)/g, "$1."); // adiciona o prefixo "R$" e o separador de milhar
    this.el.nativeElement.value = valor;
    console.log("valor: " + valor);
  }
}

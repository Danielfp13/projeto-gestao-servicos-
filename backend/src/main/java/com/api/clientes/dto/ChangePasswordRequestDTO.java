package com.api.clientes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank(message = "O campo senha Atual é obrigatorio!")
    private String senhaAtual;
    @NotBlank(message = "O campo nova Senha é obrigatorio!")
    private String novaSenha;
    @NotBlank(message = "O campo confime a nova senha é obrigatorio!")
    private String confirmaNovaSenha;
    @NotBlank(message = "O campo email é obrigatorio!")
    private String email;

}

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
public class ResetPasswordRequestDTO {

    @NotBlank(message = "O campo token é obrigatorio!")
    private String token;
    @NotBlank(message = "O campo password é obrigatorio!")
    private String password;

}

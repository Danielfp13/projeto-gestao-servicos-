package com.api.clientes.dto;

import com.api.clientes.model.entity.Cliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicoPrestadoDto {

    @NotBlank(message = "O campo descrição é obrigatorio!")
    private String descricao;

    @NotBlank(message = "O campo preço é obrigatorio!")
    private String preco;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotBlank(message = "O campo data é obrigatorio!")
    private String data;

    @NotNull(message = "O campo cliente é obrigatorio!")
    private Integer idCliente;


}

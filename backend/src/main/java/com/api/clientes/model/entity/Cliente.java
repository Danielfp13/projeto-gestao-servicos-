package com.api.clientes.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Proxy(lazy = false)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O campo nome é obrigatório.!!")
    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 14)
    @NotBlank(message = "O campo cpf é obrigatório.!!")
    @CPF(message = "O campo cpf está incorreto.!!")
    private String cpf;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    @PrePersist
    public void prePersite() {
        setDataCadastro(LocalDate.now());
    }
}

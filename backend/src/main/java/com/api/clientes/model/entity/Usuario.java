package com.api.clientes.model.entity;

import com.api.clientes.model.enums.Perfil;
import jakarta.persistence.*;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Validated
@GroupSequence(Usuario.class)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O campo email é obrigatório", groups = {Atualizacao.class, Insercao.class})
    @Email(message = "Esse não é um email válido.", groups = {Atualizacao.class, Insercao.class})
    @Column(unique = true)
    private String username;

    @Column
    @NotBlank(message = "O campo nome é obrigatório", groups = {Atualizacao.class, Insercao.class})
    private String nome;

    @Column(length = 255)
    @NotBlank(message = "O campo senha é obrigatório", groups = Insercao.class)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "perfis")
    private Set<Integer> perfis = new HashSet<>();


    public Usuario(Integer id, String username, String nome, String password) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.password = password;
        addPerfil(Perfil.USER);
    }

    public Usuario() {
        addPerfil(Perfil.USER);
    }

    public Set<Perfil> getPerfis() {
        return perfis.stream().map((x) -> Perfil.toEnum(x)).collect(Collectors.toSet());
    }

    public void addPerfil(Perfil role) {
        this.perfis.add(role.getCod());
    }

    public interface Insercao {
    }

    public interface Atualizacao {
    }

}

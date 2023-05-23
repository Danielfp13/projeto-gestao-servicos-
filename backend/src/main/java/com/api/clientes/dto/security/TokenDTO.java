package com.api.clientes.dto.security;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class TokenDTO {

    private String userName;
    private Boolean authenticate;
    private Date create;
    private Date expiration;
    private String accesToken;
    private String refreshToken;


}

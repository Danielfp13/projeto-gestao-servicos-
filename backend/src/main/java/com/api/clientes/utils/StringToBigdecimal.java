package com.api.clientes.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StringToBigdecimal {

    public BigDecimal convert(String value){
        if(value == null || value.isEmpty() ){
            return null;
        }
        value = value.replace(".","").replace(",",".").replace("R$ ","");
        try {
            return new BigDecimal(value).multiply(BigDecimal.valueOf(10));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}


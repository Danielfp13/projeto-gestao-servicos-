package com.api.clientes.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringToBigdecimalTest {

    private StringToBigdecimal stringToBigDecimal;


    @BeforeEach
    void setUp() {
        stringToBigDecimal = new StringToBigdecimal();
    }

    @Test
    @DisplayName("Conversão com valor válido retorna o BigDecimal esperado")
    void convert_ComValorValido_RetornaBigDecimalEsperado() {

        String value = "R$ 1.000,55";
        BigDecimal expected = new BigDecimal("10005.50");
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Conversão com valor nulo retorna nulo")
    void convert_ComValorNulo_RetornaNulo() {

        String value = null;
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertNull(actual);
    }

    @Test
    @DisplayName("Conversão com string qualquer retorna nulo")
    void convert_ComStringQualquer_RetornaNulo() {

        String value = "qwertyu";
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertNull(actual);
    }


}



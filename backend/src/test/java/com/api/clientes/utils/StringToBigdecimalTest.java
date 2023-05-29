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

    @Test
    @DisplayName("Conversão com uma string vazia retorna nulo")
    void convert_ComStringVazia_RetornaNulo() {
        String value = "";
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertNull(actual);
    }

    @Test
    @DisplayName("Conversão com uma string contendo apenas espaços em branco retorna nulo")
    void convert_ComStringEspacosEmBranco_RetornaNulo() {
        String value = "    ";
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertNull(actual);
    }

    @Test
    @DisplayName("Conversão com uma string contendo apenas caracteres inválidos retorna nulo")
    void convert_ComStringCaracteresInvalidos_RetornaNulo() {
        String value = "abc";
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertNull(actual);
    }

    @Test
    @DisplayName("Conversão com um valor muito grande retorna o BigDecimal esperado")
    void convert_ComValorMuitoGrande_RetornaBigDecimalEsperado() {
        String value = "1000000000000000000000000000";
        BigDecimal expected = new BigDecimal("10000000000000000000000000000");
        BigDecimal actual = stringToBigDecimal.convert(value);
        assertEquals(expected, actual);
    }
}



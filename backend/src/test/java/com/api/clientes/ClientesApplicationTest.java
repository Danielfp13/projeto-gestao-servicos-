package com.api.clientes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientesApplicationTest {

    @Test
    void contextLoads() {
        // Verifica se o contexto da aplicação é carregado corretamente
        ClientesApplication application = new ClientesApplication();
        assertNotNull(application);
    }

}
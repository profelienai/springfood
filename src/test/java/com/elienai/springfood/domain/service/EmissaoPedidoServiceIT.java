package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.PedidoNaoEncontradoException;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/EmissaoPedidoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmissaoPedidoServiceIT {

	@Autowired
	private EmissaoPedidoService emissaoPedido;
	
    @Autowired
    private DatabaseCleaner databaseCleaner;

    /**
     * Limpa o banco após cada teste, garantindo isolamento entre os testes.
     */
    @AfterEach
    private void cleanDatabase() {
    	databaseCleaner.clearTables();
    }
    
	@Test
	public void deveRetornarPedidoPorId() {
		Pedido pedido = emissaoPedido.buscarOuFalhar(1L);
		
		assertNotNull(pedido);
		assertEquals(1L, pedido.getId());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoPedidoInexistente() {
		PedidoNaoEncontradoException ex = assertThrows(PedidoNaoEncontradoException.class, () -> emissaoPedido.buscarOuFalhar(99L));
		
		assertEquals("Não existe um pedido com código 99", ex.getMessage());
	}	
}

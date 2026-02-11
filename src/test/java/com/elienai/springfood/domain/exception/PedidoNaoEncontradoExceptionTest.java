package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PedidoNaoEncontradoExceptionTest {
	@Test
	void testPedidoNaoEncontradoException_comPedidoId() {
		PedidoNaoEncontradoException ex = new PedidoNaoEncontradoException("ABC123");
		assertEquals("Não existe um pedido com código ABC123", ex.getMessage());
	}
}

package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PedidoNaoEncontradoExceptionTest {
	@Test
	void testPedidoNaoEncontradoException_comMensagem() {
		PedidoNaoEncontradoException ex = new PedidoNaoEncontradoException("Pedido não encontrado");
		assertEquals("Pedido não encontrado", ex.getMessage());
	}

	@Test
	void testPedidoNaoEncontradoException_comPedidoId() {
		PedidoNaoEncontradoException ex = new PedidoNaoEncontradoException(99L);
		assertEquals("Não existe um pedido com código 99", ex.getMessage());
	}
}

package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RestauranteNaoEncontradoExceptionTest {
	@Test
	void testRestauranteNaoEncontradoException_comMensagem() {
		RestauranteNaoEncontradoException ex = new RestauranteNaoEncontradoException("Restaurante não encontrado");
		assertEquals("Restaurante não encontrado", ex.getMessage());
	}

	@Test
	void testRestauranteNaoEncontradoException_comCidadeId() {
		RestauranteNaoEncontradoException ex = new RestauranteNaoEncontradoException(99L);
		assertEquals("Não existe um cadastro de restaurante com código 99", ex.getMessage());
	}
}

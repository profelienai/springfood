package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CozinhaNaoEncontradaExceptionTest {

	@Test
	void testCozinhaNaoEncontradaException_comMensagem() {
		CozinhaNaoEncontradaException ex = new CozinhaNaoEncontradaException("Cozinha não encontrada");
		assertEquals("Cozinha não encontrada", ex.getMessage());
	}

	@Test
	void testCozinhaNaoEncontradaException_comCozinhaId() {
		CozinhaNaoEncontradaException ex = new CozinhaNaoEncontradaException(99L);
		assertEquals("Não existe um cadastro de cozinha com código 99", ex.getMessage());
	}
	
}

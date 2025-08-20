package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CidadeNaoEncontradaExceptionTest {

	@Test
	void testCidadeNaoEncontradaException_comMensagem() {
		CidadeNaoEncontradaException ex = new CidadeNaoEncontradaException("Cidade não encontrada");
		assertEquals("Cidade não encontrada", ex.getMessage());
	}

	@Test
	void testCidadeNaoEncontradaException_comCidadeId() {
		CidadeNaoEncontradaException ex = new CidadeNaoEncontradaException(99L);
		assertEquals("Não existe um cadastro de cidade com código 99", ex.getMessage());
	}
	
}

package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ProdutoNaoEncontradoExceptionTest {
	@Test
	void testProdutoNaoEncontradoException_comMensagem() {
		ProdutoNaoEncontradoException ex = new ProdutoNaoEncontradoException("Produto não encontrado");
		assertEquals("Produto não encontrado", ex.getMessage());
	}

	@Test
	void testProdutoNaoEncontradoException_comProdutoId() {
		ProdutoNaoEncontradoException ex = new ProdutoNaoEncontradoException(1L, 2L);
		assertEquals("Não existe um cadastro de produto com código 2 para o restaurante de código 1", ex.getMessage());
	}
}

package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PermissaoNaoEncontradaExceptionTest {
	@Test
	void testPermissaoNaoEncontradoException_comMensagem() {
		var ex = new PermissaoNaoEncontradaException("Permissão não encontrada");
		assertEquals("Permissão não encontrada", ex.getMessage());
	}

	@Test
	void testPermissaoNaoEncontradoException_comPermissaoId() {
		var ex = new PermissaoNaoEncontradaException(99L);
		assertEquals("Não existe um cadastro de permissão com código 99", ex.getMessage());
	}
}

package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UsuarioNaoEncontradoExceptionTest {
	@Test
	void testUsuarioNaoEncontradoException_comMensagem() {
		UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException("Usuario não encontrado");
		assertEquals("Usuario não encontrado", ex.getMessage());
	}

	@Test
	void testUsuarioNaoEncontradoException_comUsuarioId() {
		UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException(99L);
		assertEquals("Não existe um cadastro de usuário com código 99", ex.getMessage());
	}
}

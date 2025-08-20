package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EstadoNaoEncontradoExceptionTest {
	@Test
	void testEstadoNaoEncontradoException_comMensagem() {
		EstadoNaoEncontradoException ex = new EstadoNaoEncontradoException("Estado não encontrado");
		assertEquals("Estado não encontrado", ex.getMessage());
	}

	@Test
	void testEstadoNaoEncontradoException_comEstadoId() {
		EstadoNaoEncontradoException ex = new EstadoNaoEncontradoException(99L);
		assertEquals("Não existe um cadastro de estado com código 99", ex.getMessage());
	}
}

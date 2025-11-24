package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GrupoNaoEncontradoExceptionTest {
	@Test
	void testGrupoNaoEncontradoException_comMensagem() {
		GrupoNaoEncontradoException ex = new GrupoNaoEncontradoException("Grupo não encontrado");
		assertEquals("Grupo não encontrado", ex.getMessage());
	}

	@Test
	void testGrupoNaoEncontradoException_comGrupoId() {
		GrupoNaoEncontradoException ex = new GrupoNaoEncontradoException(99L);
		assertEquals("Não existe um cadastro de grupo com código 99", ex.getMessage());
	}
}

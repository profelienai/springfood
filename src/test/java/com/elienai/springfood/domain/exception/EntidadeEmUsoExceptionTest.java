package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EntidadeEmUsoExceptionTest {

	@Test
	void testEntidadeEmUsoException() {
		EntidadeEmUsoException ex = new EntidadeEmUsoException("Entidade em uso");
		assertEquals("Entidade em uso", ex.getMessage());
	}
	
}

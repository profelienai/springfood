package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class NegocioExceptionTest {

	@Test
	void testNegocioException_comMensagem() {
		NegocioException ex = new NegocioException("Erro de negócio");
		
		assertEquals("Erro de negócio", ex.getMessage());
		assertNull(ex.getCause());
	}
	
	@Test
	void testNegocioException_comMensagemECausa() {
		Throwable causa = new RuntimeException();
		NegocioException ex = new NegocioException("Erro de negócio", causa);
		
		assertEquals("Erro de negócio", ex.getMessage());
		assertNotNull(ex.getCause());
		assertSame(causa, ex.getCause());
	}
	
}

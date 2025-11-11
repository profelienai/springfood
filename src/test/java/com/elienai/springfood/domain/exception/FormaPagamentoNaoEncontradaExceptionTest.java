package com.elienai.springfood.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FormaPagamentoNaoEncontradaExceptionTest {
	@Test
	void testFormaPagamentoNaoEncontradaException_comMensagem() {
		FormaPagamentoNaoEncontradaException ex = new FormaPagamentoNaoEncontradaException("Forma de pagamento não encontrada");
		assertEquals("Forma de pagamento não encontrada", ex.getMessage());
	}

	@Test
	void testFormaPagamentoNaoEncontradaException_comFormaPagamentoId() {
		FormaPagamentoNaoEncontradaException ex = new FormaPagamentoNaoEncontradaException(99L);
		assertEquals("Não existe um cadastro de forma de pagamento com código 99", ex.getMessage());
	}
}

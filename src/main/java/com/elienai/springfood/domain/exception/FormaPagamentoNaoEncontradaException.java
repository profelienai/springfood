package com.elienai.springfood.domain.exception;

public class FormaPagamentoNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	public FormaPagamentoNaoEncontradaException(String mensagem) {
		super(mensagem);
	}	
	
	public FormaPagamentoNaoEncontradaException(Long formaPagamentoId) {
		super(String.format("Não existe um cadastro de forma de pagamento com código %d", formaPagamentoId));
	}
	
}

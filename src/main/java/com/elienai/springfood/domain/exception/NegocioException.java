package com.elienai.springfood.domain.exception;

public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegocioException(String messagem) {
		super(messagem);
	}
	
	public NegocioException(String messagem, Throwable causa) {
		super(messagem, causa);
	}
}

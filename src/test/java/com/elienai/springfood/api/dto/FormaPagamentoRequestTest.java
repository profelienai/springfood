package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FormaPagamentoRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		var formaPagamentoRequest = new FormaPagamentoRequest();
		formaPagamentoRequest.setDescricao("Dinheiro");
		
	 	var violations =  validator.validate(formaPagamentoRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoDescricaoNulo() {
		var formaPagamentoRequest = new FormaPagamentoRequest();
		formaPagamentoRequest.setDescricao(null);
		
	 	var violations =  validator.validate(formaPagamentoRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoDescricaoEmBranco() {
		var formaPagamentoRequest = new FormaPagamentoRequest();
		formaPagamentoRequest.setDescricao("");
		
	 	var violations =  validator.validate(formaPagamentoRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
}

package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EstadoRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		EstadoRequest estadoRequest = new EstadoRequest();
		estadoRequest.setNome("Rio de Janeiro");
		
	 	Set<ConstraintViolation<EstadoRequest>> violations =  validator.validate(estadoRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoNomeNulo() {
		EstadoRequest estadoRequest = new EstadoRequest();
		estadoRequest.setNome(null);
		
	 	Set<ConstraintViolation<EstadoRequest>> violations =  validator.validate(estadoRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoNomeEmBranco() {
		EstadoRequest estadoRequest = new EstadoRequest();
		estadoRequest.setNome("");
		
	 	Set<ConstraintViolation<EstadoRequest>> violations =  validator.validate(estadoRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
}

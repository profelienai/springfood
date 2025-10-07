package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CozinhaRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		var factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		var cozinhaRequest = new CozinhaRequest();
		cozinhaRequest.setNome("Rio de Janeiro");
		
	 	var violations = validator.validate(cozinhaRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoNomeNulo() {
		var cozinhaRequest = new CozinhaRequest();
		cozinhaRequest.setNome(null);
		
	 	var violations = validator.validate(cozinhaRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoNomeEmBranco() {
		var cozinhaRequest = new CozinhaRequest();
		cozinhaRequest.setNome("");
		
	 	var violations = validator.validate(cozinhaRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}	
	
}

package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CozinhaIdRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		var factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(1L);
		
	 	var violations =  validator.validate(cozinhaIdRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoIdNulo() {
	    var cozinha = new CozinhaIdRequest();
	    cozinha.setId(null);

	    var violations = validator.validate(cozinha);

	    assertThat(violations).isNotEmpty();
	}
}

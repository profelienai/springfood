package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CidadeIdRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		var cidadeIdRequest = new CidadeIdRequest();
		cidadeIdRequest.setId(1L);
		
	 	var violations =  validator.validate(cidadeIdRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoIdNulo() {
	    CidadeIdRequest cidadeIdRequest = new CidadeIdRequest();
	    cidadeIdRequest.setId(null);

	    var violations = validator.validate(cidadeIdRequest);

	    assertThat(violations)
	    	.hasSize(1)
	    	.extracting(v -> v.getPropertyPath().toString())
	    	.contains("id");	    
	}
}

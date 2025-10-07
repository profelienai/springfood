package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EstadoIdRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		EstadoIdRequest estadoIdRequest = new EstadoIdRequest();
		estadoIdRequest.setId(1L);
		
	 	Set<ConstraintViolation<EstadoIdRequest>> violations =  validator.validate(estadoIdRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoIdNulo() {
	    EstadoIdRequest estado = new EstadoIdRequest();
	    estado.setId(null);

	    Set<ConstraintViolation<EstadoIdRequest>> violations = validator.validate(estado);

	    assertThat(violations).isNotEmpty();
	}
}

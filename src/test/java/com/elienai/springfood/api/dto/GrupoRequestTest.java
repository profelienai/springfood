package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GrupoRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		GrupoRequest grupoRequest = new GrupoRequest();
		grupoRequest.setNome("Grupo 1");
		
	 	var violations =  validator.validate(grupoRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoNomeNulo() {
		GrupoRequest grupoRequest = new GrupoRequest();
		grupoRequest.setNome(null);
		
	 	var violations =  validator.validate(grupoRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoNomeEmBranco() {
		GrupoRequest grupoRequest = new GrupoRequest();
		grupoRequest.setNome("");
		
	 	var violations =  validator.validate(grupoRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
}

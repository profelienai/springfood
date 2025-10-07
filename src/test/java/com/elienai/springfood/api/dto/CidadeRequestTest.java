package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CidadeRequestTest {

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
		
		CidadeRequest cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome("Rio de Janeiro");
		cidadeRequest.setEstado(estadoIdRequest);
		
	 	Set<ConstraintViolation<CidadeRequest>> violations =  validator.validate(cidadeRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoNomeNulo() {
		EstadoIdRequest estadoIdRequest = new EstadoIdRequest();
		estadoIdRequest.setId(1L);
		
		CidadeRequest cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome(null);
		cidadeRequest.setEstado(estadoIdRequest);
		
	 	Set<ConstraintViolation<CidadeRequest>> violations =  validator.validate(cidadeRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoNomeEmBranco() {
		EstadoIdRequest estadoIdRequest = new EstadoIdRequest();
		estadoIdRequest.setId(1L);
		
		CidadeRequest cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome("");
		cidadeRequest.setEstado(estadoIdRequest);
		
	 	Set<ConstraintViolation<CidadeRequest>> violations =  validator.validate(cidadeRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco");
	}	
	
	@Test
	void deveFalhar_quandoEstadoNulo() {
		CidadeRequest cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome("Rio de Janeiro");
		cidadeRequest.setEstado(null);
		
	 	Set<ConstraintViolation<CidadeRequest>> violations =  validator.validate(cidadeRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve ser nulo");
	}	
	
	@Test
	void deveFalhar_quandoTodosDadosNulos() {
		CidadeRequest cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome(null);
		cidadeRequest.setEstado(null);
		
	 	Set<ConstraintViolation<CidadeRequest>> violations =  validator.validate(cidadeRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(2)
	 		.extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco", "não deve ser nulo");
	}	
}

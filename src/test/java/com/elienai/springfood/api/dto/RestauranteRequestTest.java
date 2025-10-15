package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RestauranteRequestTest {

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
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
	 	var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoNomeNulo() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(1L);
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome(null);
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoNomeEmBranco() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(1L);
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco");
	}	
	
	@Test
	void deveFalhar_quandoTaxaFreteNulo() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(1L);
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(null);
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve ser nulo");
	}
	
	@Test
	void deveFalhar_quandoTaxaFreteNegativo() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(1L);
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(-10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("deve ser maior ou igual a 0");
	}		
	
	@Test
	void deveFalhar_quandoCozinhaNulo() {
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(null);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(1)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("não deve ser nulo");
	}	
	
	@Test
	void deveFalhar_quandoTodosDadosNulos() {
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome(null);
		restauranteRequest.setTaxaFrete(null);
		restauranteRequest.setCozinha(null);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations)
	 		.hasSize(3)
	 		.extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco", "não deve ser nulo", "não deve ser nulo");
	}	
}

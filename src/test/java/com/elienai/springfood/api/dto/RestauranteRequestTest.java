package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RestauranteRequestTest {

	private static EnderecoRequest endereco;
	private static CozinhaIdRequest cozinha;
	
	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		var factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	
        var cidade = new CidadeIdRequest();
        cidade.setId(1L);

        endereco = new EnderecoRequest();
        endereco.setCep("38400-999");
        endereco.setLogradouro("Rua João Pinheiro");
        endereco.setNumero("1000");
        endereco.setComplemento("C1");
        endereco.setBairro("Centro");
        endereco.setCidade(cidade);
        
        cozinha = new CozinhaIdRequest();
        cozinha.setId(1L);		
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinha);
		restauranteRequest.setEndereco(endereco);
		
	 	var violations =  validator.validate(restauranteRequest);
	 	
	 	assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoCamposObrigatoriosNulos() {
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome(null);
		restauranteRequest.setTaxaFrete(null);
		restauranteRequest.setCozinha(null);
		restauranteRequest.setEndereco(null);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	    assertThat(violations)
	    	.hasSize(4)
	    	.extracting(v -> v.getPropertyPath().toString())
	    	.contains("nome", "taxaFrete", "cozinha", "endereco");		
		
	 	assertThat(violations)
	 		.extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco", "não deve ser nulo", "não deve ser nulo");
	}	
	
	@Test
	void deveFalhar_quandoNomeEmBranco() {
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinha);
		restauranteRequest.setEndereco(endereco);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	    assertThat(violations)
    		.hasSize(1)
    		.extracting(v -> v.getPropertyPath().toString())
    		.contains("nome");				
		
	 	assertThat(violations)
	 		.extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco");
	}	
	
	@Test
	void deveFalhar_quandoTaxaFreteNegativo() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(1L);
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(-10.99d));
		restauranteRequest.setCozinha(cozinha);
		restauranteRequest.setEndereco(endereco);
		
		var violations =  validator.validate(restauranteRequest);
	 	
	    assertThat(violations)
			.hasSize(1)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("taxaFrete");		
		
	 	assertThat(violations)
	 		.extracting(ConstraintViolation::getMessage)
	 		.containsExactly("deve ser maior ou igual a 0");
	}	
}

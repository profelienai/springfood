package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UsuarioRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		var factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoCamposPreenchidos() {
		var usuario = new UsuarioRequest();
		usuario.setNome("João Silva");
		usuario.setEmail("joao@email.com");
		
		var violations = validator.validate(usuario);
		
		assertThat(violations).isEmpty();
	}
	
	@Test
	void deveFalhar_quandoCamposObrigatoriosNulos() {
		var usuario = new UsuarioRequest();
		usuario.setNome(null);
		usuario.setEmail(null);
		
		var violations = validator.validate(usuario);
		
		assertThat(violations)
			.hasSize(2)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("nome", "email");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("não deve estar em branco");
	}
	
	@Test
	void deveFalhar_quandoEmailInvalido() {
		var usuario = new UsuarioRequest();
		usuario.setNome("Maria");
		usuario.setEmail("email-invalido");

		var violations = validator.validate(usuario);

		assertThat(violations)
			.hasSize(1)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("email");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("deve ser um endereço de e-mail bem formado");
	}
	
	@Test
	void deveFalhar_quandoNomeOuEmailEmBranco() {
		var usuario = new UsuarioRequest();
		usuario.setNome("");
		usuario.setEmail("");

		var violations = validator.validate(usuario);

		assertThat(violations)
			.hasSize(2)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("nome", "email");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("não deve estar em branco");
	}
}

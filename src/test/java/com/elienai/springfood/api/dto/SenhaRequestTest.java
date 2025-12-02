package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SenhaRequestTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUp() {
		var factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void deveSerValido_quandoCamposPreenchidos() {
		var senhaRequest = new SenhaRequest();
		senhaRequest.setSenhaAtual("123");
		senhaRequest.setNovaSenha("abc123");

		var violations = validator.validate(senhaRequest);

		assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoCamposObrigatoriosNulos() {
		var senhaRequest = new SenhaRequest();
		senhaRequest.setSenhaAtual(null);
		senhaRequest.setNovaSenha(null);

		var violations = validator.validate(senhaRequest);

		assertThat(violations)
			.hasSize(2)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("senhaAtual", "novaSenha");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("não deve estar em branco");
	}

	@Test
	void deveFalhar_quandoCamposEmBranco() {
		var senhaRequest = new SenhaRequest();
		senhaRequest.setSenhaAtual("");
		senhaRequest.setNovaSenha("");

		var violations = validator.validate(senhaRequest);

		assertThat(violations)
			.hasSize(2)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("senhaAtual", "novaSenha");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("não deve estar em branco");
	}
}

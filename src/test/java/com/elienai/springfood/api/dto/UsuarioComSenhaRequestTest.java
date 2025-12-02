package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UsuarioComSenhaRequestTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		var factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		var usuario = new UsuarioComSenhaRequest();
		usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("minhaSenha123");

		var violations = validator.validate(usuario);

		assertThat(violations).isEmpty();
	}

	@Test
	void deveFalhar_quandoCamposObrigatoriosNulos() {
		var usuario = new UsuarioComSenhaRequest();
		usuario.setNome(null);
		usuario.setEmail(null);
		usuario.setSenha(null);

		var violations = validator.validate(usuario);

		assertThat(violations)
			.hasSize(3)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("nome", "email", "senha");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("não deve estar em branco");
	}

	@Test
	void deveFalhar_quandoCamposEmBranco() {
		var usuario = new UsuarioComSenhaRequest();
		usuario.setNome("");
		usuario.setEmail("");
		usuario.setSenha("");

		var violations = validator.validate(usuario);

		assertThat(violations)
			.hasSize(3)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("nome", "email", "senha");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("não deve estar em branco");
	}

	@Test
	void deveFalhar_quandoEmailInvalido() {
		var usuario = new UsuarioComSenhaRequest();
		usuario.setNome("Maria");
		usuario.setEmail("email-invalido");
		usuario.setSenha("senha123");

		var violations = validator.validate(usuario);

		assertThat(violations)
			.hasSize(1)
			.extracting(v -> v.getPropertyPath().toString())
			.contains("email");

		assertThat(violations)
			.extracting(ConstraintViolation::getMessage)
			.contains("deve ser um endereço de e-mail bem formado");
	}
}

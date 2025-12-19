package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProdutoRequestTest {

    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValido_quandoTodosCamposPreenchidos() {
        var request = new ProdutoRequest();
        request.setNome("Pizza");
        request.setDescricao("Pizza de calabresa");
        request.setPreco(new BigDecimal("39.90"));
        request.setAtivo(true);

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalhar_quandoCamposObrigatoriosNulos() {
        var request = new ProdutoRequest();
        request.setNome(null);
        request.setDescricao(null);
        request.setPreco(null);
        request.setAtivo(null);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(4)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("nome", "descricao", "preco", "ativo");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("não deve estar em branco", "não deve estar em branco", "não deve ser nulo", "não deve ser nulo");
    }

    @Test
    void deveFalhar_quandoNomeEmBranco() {
        var request = new ProdutoRequest();
        request.setNome("");
        request.setDescricao("Descrição válida");
        request.setPreco(new BigDecimal("10.00"));
        request.setAtivo(true);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(1)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("nome");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("não deve estar em branco");
    }

    @Test
    void deveFalhar_quandoDescricaoEmBranco() {
        var request = new ProdutoRequest();
        request.setNome("Pizza");
        request.setDescricao("");
        request.setPreco(new BigDecimal("10.00"));
        request.setAtivo(true);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(1)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("descricao");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("não deve estar em branco");
    }

    @Test
    void deveFalhar_quandoPrecoNegativo() {
        var request = new ProdutoRequest();
        request.setNome("Pizza");
        request.setDescricao("Pizza de queijo");
        request.setPreco(new BigDecimal("-5.00"));
        request.setAtivo(true);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(1)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("preco");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("deve ser maior ou igual a 0");
    }
}

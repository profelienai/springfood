package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ItemPedidoRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValido_quandoCamposObrigatoriosPreenchidos() {
        var request = new ItemPedidoRequest();
        request.setProdutoId(1L);
        request.setQuantidade(2);
        request.setObservacao("Sem cebola");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalhar_quandoCamposObrigatoriosNulos() {
        var request = new ItemPedidoRequest();
        request.setProdutoId(null);
        request.setQuantidade(null);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(2)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("produtoId", "quantidade");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("não deve ser nulo");
    }

    @Test
    void deveFalhar_quandoQuantidadeNegativa() {
        var request = new ItemPedidoRequest();
        request.setProdutoId(1L);
        request.setQuantidade(-1);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(1)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("quantidade");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("deve ser maior ou igual a 0");
    }
}

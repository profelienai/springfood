package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CozinhaTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValida_quandoTodosCamposPreenchidos() {
        Cozinha cozinha = new Cozinha();
        cozinha.setId(1L);
        cozinha.setNome("Italiana");

        Set<ConstraintViolation<Cozinha>> violations = validator.validate(cozinha);

        assertThat(violations).isEmpty();
        assertThat(cozinha.getRestaurantes()).isNotNull();
    }

    @Test
    void deveFalhar_quandoNomeNulo() {
        Cozinha cozinha = new Cozinha();
        cozinha.setId(1L);
        cozinha.setNome(null);

        Set<ConstraintViolation<Cozinha>> violations = validator.validate(cozinha);

        assertThat(violations)
        	.hasSize(1)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("não deve estar em branco");
    }

    @Test
    void deveFalhar_quandoNomeEmBranco() {
        Cozinha cozinha = new Cozinha();
        cozinha.setId(1L);
        cozinha.setNome("   ");

        Set<ConstraintViolation<Cozinha>> violations = validator.validate(cozinha);

        assertThat(violations)
        	.hasSize(1)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("não deve estar em branco");
    }

    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        Cozinha c1 = new Cozinha();
        c1.setId(1L);
        c1.setNome("Italiana");

        Cozinha c2 = new Cozinha();
        c2.setId(1L);
        c2.setNome("Japonesa");

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).hasSameHashCodeAs(c2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        Cozinha c1 = new Cozinha();
        c1.setId(1L);

        Cozinha c2 = new Cozinha();
        c2.setId(2L);

        assertThat(c1).isNotEqualTo(c2);
    }
}
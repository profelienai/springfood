package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RestauranteIdRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValido_quandoIdInformado() {
        var request = new RestauranteIdRequest();
        request.setId(1L);

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalhar_quandoIdNulo() {
        var request = new RestauranteIdRequest();
        request.setId(null);

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(1)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("id");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("não deve ser nulo");
    }
}

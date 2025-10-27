package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EstadoTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		Estado estado = new Estado();
		estado.setId(1L);
		estado.setNome("Rio de Janeiro");
		
		Set<ConstraintViolation<Estado>> violations = validator.validate(estado);
		
		assertThat(violations).isEmpty();
	}
	
    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        Estado e1 = new Estado();
        e1.setId(1L);
        e1.setNome("São Paulo");

        Estado e2 = new Estado();
        e2.setId(1L);
        e2.setNome("Rio de Janeiro");

        assertThat(e1).isEqualTo(e2);
        assertThat(e1).hasSameHashCodeAs(e2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        Estado e1 = new Estado();
        e1.setId(1L);

        Estado e2 = new Estado();
        e2.setId(2L);

        assertThat(e1).isNotEqualTo(e2);
        assertThat(e1).doesNotHaveSameHashCodeAs(e2);
    }	
}

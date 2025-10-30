package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FormaPagamentoTest {

	private static Validator validator;
	
	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void deveSerValido_quandoTodosCamposPreenchidos() {
		FormaPagamento formaPagamento = new FormaPagamento();
		formaPagamento.setId(1L);
		formaPagamento.setDescricao("PIX");
		
		Set<ConstraintViolation<FormaPagamento>> violations = validator.validate(formaPagamento);
		
		assertThat(violations).isEmpty();
	}
	
    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        FormaPagamento f1 = new FormaPagamento();
        f1.setId(1L);
        f1.setDescricao("Dinheiro");

        FormaPagamento f2 = new FormaPagamento();
        f2.setId(1L);
        f2.setDescricao("PIX");

        assertThat(f1).isEqualTo(f2);
        assertThat(f1).hasSameHashCodeAs(f2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        FormaPagamento f1 = new FormaPagamento();
        f1.setId(1L);

        FormaPagamento f2 = new FormaPagamento();
        f2.setId(2L);

        assertThat(f1).isNotEqualTo(f2);
        assertThat(f1).doesNotHaveSameHashCodeAs(f2);
    }	
}

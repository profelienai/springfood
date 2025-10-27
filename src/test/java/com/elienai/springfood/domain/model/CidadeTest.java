package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CidadeTest {

	public static Validator validator;
	
	@BeforeAll
	static void setUpValidate() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	private Estado getEstadoValido() {
		Estado estado = new Estado();
		estado.setId(1L);
		estado.setNome("Rio de Janeiro");
		
		return estado;
	}
	
	@Test
	void deveSerValida_quandoTodosCamposPreenchidos() {
		Cidade cidade = new Cidade();
		cidade.setId(1L);
		cidade.setNome("Volta Redonda");
		cidade.setEstado(getEstadoValido());
		
		Set<ConstraintViolation<Cidade>> violations = validator.validate(cidade);
		
		assertThat(violations).isEmpty();
	}
	
    @Test
    void deveSerIgual_quandoIdsForemIguais() {
    	Cidade c1 = new Cidade();
        c1.setId(1L);
        c1.setNome("São Paulo");

        Cidade c2 = new Cidade();
        c2.setId(1L);
        c2.setNome("Rio de Janeiro");

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).hasSameHashCodeAs(c2);
        
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
    	Cidade c1 = new Cidade();
        c1.setId(1L);

        Cidade c2 = new Cidade();
        c2.setId(2L);

        assertThat(c1).isNotEqualTo(c2);
        assertThat(c1).doesNotHaveSameHashCodeAs(c2);
    }		
}

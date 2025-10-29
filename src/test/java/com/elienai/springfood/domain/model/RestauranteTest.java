package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RestauranteTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Cozinha getCozinhaValida() {
        Cozinha cozinha = new Cozinha();
        cozinha.setId(1L);
        cozinha.setNome("Italiana");
        return cozinha;
    }

    @Test
    void deveSerValido_quandoTodosCamposPreenchidos() {
        Restaurante restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Bella Napoli");
        restaurante.setTaxaFrete(BigDecimal.valueOf(10));
        restaurante.setCozinha(getCozinhaValida());

        Set<ConstraintViolation<Restaurante>> violations = validator.validate(restaurante);

        assertThat(violations).isEmpty();
        assertThat(restaurante.getFormasPagamento()).isNotNull();
        assertThat(restaurante.getProdutos()).isNotNull();
    }

    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        Restaurante r1 = new Restaurante();
        r1.setId(1L);

        Restaurante r2 = new Restaurante();
        r2.setId(1L);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).hasSameHashCodeAs(r2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        Restaurante r1 = new Restaurante();
        r1.setId(1L);

        Restaurante r2 = new Restaurante();
        r2.setId(2L);

        assertThat(r1).isNotEqualTo(r2);
    }
    
    @Test
    void deveAtivarRestaurante_quandoChamarMetodoAtivar() {
        Restaurante restaurante = new Restaurante();
        restaurante.setAtivo(false);

        restaurante.ativar();

        assertThat(restaurante.getAtivo()).isTrue();
    }

    @Test
    void deveInativarRestaurante_quandoChamarMetodoInativar() {
        Restaurante restaurante = new Restaurante();
        restaurante.setAtivo(true);

        restaurante.inativar();

        assertThat(restaurante.getAtivo()).isFalse();
    }    
}

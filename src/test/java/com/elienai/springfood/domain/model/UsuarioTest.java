package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValido_quandoTodosCamposPreenchidos() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João da Silva");
        usuario.setEmail("joao@exemplo.com");
        usuario.setSenha("123456");

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

        assertThat(violations).isEmpty();
        assertThat(usuario.getGrupos()).isNotNull();
    }

    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        Usuario u1 = new Usuario();
        u1.setId(1L);

        Usuario u2 = new Usuario();
        u2.setId(1L);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1).hasSameHashCodeAs(u2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        Usuario u1 = new Usuario();
        u1.setId(1L);

        Usuario u2 = new Usuario();
        u2.setId(2L);

        assertThat(u1).isNotEqualTo(u2);
    }

    @Test
    void deveRetornarTrue_quandoSenhaCoincidir() {
        Usuario usuario = new Usuario();
        usuario.setSenha("senha-secreta");

        boolean coincide = usuario.senhaCoincideCom("senha-secreta");

        assertThat(coincide).isTrue();
    }

    @Test
    void deveRetornarFalse_quandoSenhaNaoCoincidir() {
        Usuario usuario = new Usuario();
        usuario.setSenha("senha-secreta");

        boolean coincide = usuario.senhaCoincideCom("outra-senha");

        assertThat(coincide).isFalse();
    }

    @Test
    void deveRetornarTrue_quandoSenhaNaoCoincidirUsandoMetodoNegado() {
        Usuario usuario = new Usuario();
        usuario.setSenha("senha-secreta");

        boolean naoCoincide = usuario.senhaNaoCoincideCom("outra-senha");

        assertThat(naoCoincide).isTrue();
    }

    @Test
    void deveRetornarFalse_quandoSenhaNaoCoincidirUsandoMetodoNegado() {
        Usuario usuario = new Usuario();
        usuario.setSenha("senha-secreta");

        boolean naoCoincide = usuario.senhaNaoCoincideCom("senha-secreta");

        assertThat(naoCoincide).isFalse();
    }
}

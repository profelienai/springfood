package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GrupoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValido_quandoTodosCamposPreenchidos() {
        Grupo grupo = new Grupo();
        grupo.setId(1L);
        grupo.setNome("Administradores");

        Set<ConstraintViolation<Grupo>> violations = validator.validate(grupo);

        assertThat(violations).isEmpty();
        assertThat(grupo.getPermissoes()).isNotNull();
    }

    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        Grupo g1 = new Grupo();
        g1.setId(1L);

        Grupo g2 = new Grupo();
        g2.setId(1L);

        assertThat(g1).isEqualTo(g2);
        assertThat(g1).hasSameHashCodeAs(g2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        Grupo g1 = new Grupo();
        g1.setId(1L);

        Grupo g2 = new Grupo();
        g2.setId(2L);

        assertThat(g1).isNotEqualTo(g2);
    }

    @Test
    void deveAdicionarPermissao_quandoPermissaoNaoExistir() {
        Grupo grupo = new Grupo();
        Permissao permissao = new Permissao();
        permissao.setId(1L);
        permissao.setNome("CONSULTAR_USUARIOS");

        boolean adicionou = grupo.adicionarPermissao(permissao);

        assertThat(adicionou).isTrue();
        assertThat(grupo.getPermissoes())
            .contains(permissao);
    }

    @Test
    void naoDeveAdicionarPermissao_quandoPermissaoJaExistir() {
        Grupo grupo = new Grupo();
        Permissao permissao = new Permissao();
        permissao.setId(1L);

        grupo.adicionarPermissao(permissao);
        boolean adicionou = grupo.adicionarPermissao(permissao);

        assertThat(adicionou).isFalse();
        assertThat(grupo.getPermissoes())
            .hasSize(1)
            .contains(permissao);
    }

    @Test
    void deveRemoverPermissao_quandoPermissaoExistir() {
        Grupo grupo = new Grupo();
        Permissao permissao = new Permissao();
        permissao.setId(1L);

        grupo.adicionarPermissao(permissao);
        boolean removeu = grupo.removerPermissao(permissao);

        assertThat(removeu).isTrue();
        assertThat(grupo.getPermissoes())
            .doesNotContain(permissao);
    }

    @Test
    void naoDeveRemoverPermissao_quandoPermissaoNaoExistir() {
        Grupo grupo = new Grupo();
        Permissao permissao = new Permissao();
        permissao.setId(1L);

        boolean removeu = grupo.removerPermissao(permissao);

        assertThat(removeu).isFalse();
        assertThat(grupo.getPermissoes())
            .isEmpty();
    }
}

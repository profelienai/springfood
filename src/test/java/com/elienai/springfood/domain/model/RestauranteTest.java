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
    
    @Test
    void deveAbrirRestaurante_quandoChamarMetodoAbrir() {
        Restaurante restaurante = new Restaurante();
        restaurante.setAberto(false);

        restaurante.abrir();

        assertThat(restaurante.getAberto()).isTrue();
    }

    @Test
    void deveFecharRestaurante_quandoChamarMetodoFechar() {
        Restaurante restaurante = new Restaurante();
        restaurante.setAberto(true);

        restaurante.fechar();

        assertThat(restaurante.getAberto()).isFalse();
    } 
    
    @Test
    void deveAdicionarFormaPagamento_quandoFormaPagamentoNaoExistir() {
        Restaurante restaurante = new Restaurante();
        FormaPagamento pagamento = new FormaPagamento();
        pagamento.setId(1L);
        pagamento.setDescricao("Cartão de Crédito");

        boolean adicionou = restaurante.adicionarFormaPagamento(pagamento);

        assertThat(adicionou).isTrue();
        assertThat(restaurante.getFormasPagamento())
            .contains(pagamento);
    }

    @Test
    void naoDeveAdicionarFormaPagamento_quandoFormaPagamentoJaExistir() {
        Restaurante restaurante = new Restaurante();
        FormaPagamento pagamento = new FormaPagamento();
        pagamento.setId(1L);

        restaurante.adicionarFormaPagamento(pagamento);
        boolean adicionou = restaurante.adicionarFormaPagamento(pagamento);

        assertThat(adicionou).isFalse();
        assertThat(restaurante.getFormasPagamento())
            .hasSize(1)
            .contains(pagamento);
    }

    @Test
    void deveRemoverFormaPagamento_quandoFormaPagamentoExistir() {
        Restaurante restaurante = new Restaurante();
        FormaPagamento pagamento = new FormaPagamento();
        pagamento.setId(1L);

        restaurante.adicionarFormaPagamento(pagamento);
        boolean removeu = restaurante.removerFormaPagamento(pagamento);

        assertThat(removeu).isTrue();
        assertThat(restaurante.getFormasPagamento())
            .doesNotContain(pagamento);
    }

    @Test
    void naoDeveRemoverFormaPagamento_quandoFormaPagamentoNaoExistir() {
        Restaurante restaurante = new Restaurante();
        FormaPagamento pagamento = new FormaPagamento();
        pagamento.setId(1L);

        boolean removeu = restaurante.removerFormaPagamento(pagamento);

        assertThat(removeu).isFalse();
        assertThat(restaurante.getFormasPagamento())
            .isEmpty();
    }    
    
    @Test
    void deveAdicionarResponsavel_quandoResponsavelNaoExistir() {
        Restaurante restaurante = new Restaurante();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João da Silva");

        boolean adicionou = restaurante.adicionarResponsavel(usuario);

        assertThat(adicionou).isTrue();
        assertThat(restaurante.getResponsaveis())
            .contains(usuario);
    }

    @Test
    void naoDeveAdicionarResponsavel_quandoResponsavelJaExistir() {
        Restaurante restaurante = new Restaurante();
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        restaurante.adicionarResponsavel(usuario);
        boolean adicionou = restaurante.adicionarResponsavel(usuario);

        assertThat(adicionou).isFalse();
        assertThat(restaurante.getResponsaveis())
            .hasSize(1)
            .contains(usuario);
    }

    @Test
    void deveRemoverResponsavel_quandoResponsavelExistir() {
        Restaurante restaurante = new Restaurante();
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        restaurante.adicionarResponsavel(usuario);
        boolean removeu = restaurante.removerResponsavel(usuario);

        assertThat(removeu).isTrue();
        assertThat(restaurante.getResponsaveis())
            .doesNotContain(usuario);
    }

    @Test
    void naoDeveRemoverResponsavel_quandoResponsavelNaoExistir() {
        Restaurante restaurante = new Restaurante();
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        boolean removeu = restaurante.removerResponsavel(usuario);

        assertThat(removeu).isFalse();
        assertThat(restaurante.getResponsaveis())
            .isEmpty();
    }
    
}

package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusPedidoEnumTest {

    // =====================================================
    // ESTRUTURA DO ENUM
    // =====================================================

    @Test
    void deveConterTodosOsStatusNaOrdemEsperada() {
        assertThat(StatusPedidoEnum.values())
            .containsExactly(
                StatusPedidoEnum.CRIADO,
                StatusPedidoEnum.CONFIRMADO,
                StatusPedidoEnum.ENTREGUE,
                StatusPedidoEnum.CANCELADO
            );
    }

    @Test
    void deveRetornarDescricaoCorreta_paraCadaStatus() {
        assertThat(StatusPedidoEnum.CRIADO.getDescricao())
            .isEqualTo("Criado");

        assertThat(StatusPedidoEnum.CONFIRMADO.getDescricao())
            .isEqualTo("Confirmado");

        assertThat(StatusPedidoEnum.ENTREGUE.getDescricao())
            .isEqualTo("Entregue");

        assertThat(StatusPedidoEnum.CANCELADO.getDescricao())
            .isEqualTo("Cancelado");
    }

    // =====================================================
    // REGRAS DE TRANSIÇÃO
    // =====================================================

    @Test
    void devePermitirTransicaoDeCriadoParaConfirmado() {
        boolean naoPode = StatusPedidoEnum.CRIADO
                .naoPodeAlterarPara(StatusPedidoEnum.CONFIRMADO);

        assertThat(naoPode).isFalse();
    }

    @Test
    void devePermitirTransicaoDeConfirmadoParaEntregue() {
        boolean naoPode = StatusPedidoEnum.CONFIRMADO
                .naoPodeAlterarPara(StatusPedidoEnum.ENTREGUE);

        assertThat(naoPode).isFalse();
    }

    @Test
    void devePermitirTransicaoDeCriadoParaCancelado() {
        boolean naoPode = StatusPedidoEnum.CRIADO
                .naoPodeAlterarPara(StatusPedidoEnum.CANCELADO);

        assertThat(naoPode).isFalse();
    }

    // =====================================================
    // TRANSIÇÕES INVÁLIDAS
    // =====================================================

    @Test
    void naoDevePermitirTransicaoDeCriadoParaEntregue() {
        boolean naoPode = StatusPedidoEnum.CRIADO
                .naoPodeAlterarPara(StatusPedidoEnum.ENTREGUE);

        assertThat(naoPode).isTrue();
    }

    @Test
    void naoDevePermitirTransicaoDeConfirmadoParaCancelado() {
        boolean naoPode = StatusPedidoEnum.CONFIRMADO
                .naoPodeAlterarPara(StatusPedidoEnum.CANCELADO);

        assertThat(naoPode).isTrue();
    }

    @Test
    void naoDevePermitirTransicaoDeEntregueParaQualquerOutroStatus() {
        assertThat(StatusPedidoEnum.ENTREGUE
                .naoPodeAlterarPara(StatusPedidoEnum.CRIADO))
            .isTrue();

        assertThat(StatusPedidoEnum.ENTREGUE
                .naoPodeAlterarPara(StatusPedidoEnum.CONFIRMADO))
            .isTrue();

        assertThat(StatusPedidoEnum.ENTREGUE
                .naoPodeAlterarPara(StatusPedidoEnum.CANCELADO))
            .isTrue();
    }

    @Test
    void naoDevePermitirTransicaoDeCanceladoParaQualquerOutroStatus() {
        assertThat(StatusPedidoEnum.CANCELADO
                .naoPodeAlterarPara(StatusPedidoEnum.CRIADO))
            .isTrue();

        assertThat(StatusPedidoEnum.CANCELADO
                .naoPodeAlterarPara(StatusPedidoEnum.CONFIRMADO))
            .isTrue();

        assertThat(StatusPedidoEnum.CANCELADO
                .naoPodeAlterarPara(StatusPedidoEnum.ENTREGUE))
            .isTrue();
    }
}

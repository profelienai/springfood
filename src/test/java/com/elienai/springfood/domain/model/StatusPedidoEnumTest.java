package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusPedidoEnumTest {

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
}

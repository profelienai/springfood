package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class PedidoTest {
    @Test
    void deveIniciarComStatusCriado() {
        Pedido pedido = new Pedido();

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.CRIADO);
    }

    @Test
    void deveCalcularSubtotalEValorTotal_quandoItensExistirem() {
        Pedido pedido = new Pedido();
        pedido.setTaxaFrete(new BigDecimal("5.00"));

        ItemPedido item1 = criarItem(new BigDecimal("10.00"), 2);
        ItemPedido item2 = criarItem(new BigDecimal("5.00"), 1);

        item1.setPedido(pedido);
        item2.setPedido(pedido);

        pedido.setItens(List.of(item1, item2));

        pedido.calcularValorTotal();

        assertThat(pedido.getSubtotal())
            .isEqualByComparingTo("25.00");

        assertThat(pedido.getValorTotal())
            .isEqualByComparingTo("30.00");
    }

    @Test
    void deveCalcularValorTotal_quandoMesmoSemItens() {
        Pedido pedido = new Pedido();
        pedido.setTaxaFrete(new BigDecimal("10.00"));

        pedido.calcularValorTotal();

        assertThat(pedido.getSubtotal())
            .isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(pedido.getValorTotal())
            .isEqualByComparingTo("10.00");
    }

    @Test
    void deveCalcularValorTotal_quandoTaxaFreteForZero() {
        Pedido pedido = new Pedido();
        pedido.setTaxaFrete(BigDecimal.ZERO);

        ItemPedido item = criarItem(new BigDecimal("20.00"), 1);
        item.setPedido(pedido);

        pedido.getItens().add(item);

        pedido.calcularValorTotal();

        assertThat(pedido.getSubtotal())
            .isEqualByComparingTo("20.00");

        assertThat(pedido.getValorTotal())
            .isEqualByComparingTo("20.00");
    }

    private ItemPedido criarItem(BigDecimal precoUnitario, Integer quantidade) {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(precoUnitario);
        item.setQuantidade(quantidade);
        return item;
    }

    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        Pedido p1 = new Pedido();
        p1.setId(1L);

        Pedido p2 = new Pedido();
        p2.setId(1L);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).hasSameHashCodeAs(p2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        Pedido p1 = new Pedido();
        p1.setId(1L);

        Pedido p2 = new Pedido();
        p2.setId(2L);

        assertThat(p1).isNotEqualTo(p2);
    }
}

package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemPedidoTest {

    @Test
    void deveCalcularPrecoTotal_quandoPrecoUnitarioEQuantidadeInformados() {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(new BigDecimal("10.00"));
        item.setQuantidade(3);

        item.calcularPrecoTotal();

        assertThat(item.getPrecoTotal())
            .isEqualByComparingTo("30.00");
    }

    @Test
    void deveCalcularPrecoTotalComoZero_quandoPrecoUnitarioForNulo() {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(null);
        item.setQuantidade(5);

        item.calcularPrecoTotal();

        assertThat(item.getPrecoTotal())
            .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void deveCalcularPrecoTotalComoZero_quandoQuantidadeForNula() {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(new BigDecimal("10.00"));
        item.setQuantidade(null);

        item.calcularPrecoTotal();

        assertThat(item.getPrecoTotal())
            .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void deveCalcularPrecoTotalComoZero_quandoPrecoUnitarioEQuantidadeForemNulos() {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(null);
        item.setQuantidade(null);

        item.calcularPrecoTotal();

        assertThat(item.getPrecoTotal())
            .isEqualByComparingTo(BigDecimal.ZERO);
    }	
	
    @Test
    void deveSerIgual_quandoIdsForemIguais() {
        ItemPedido item1 = new ItemPedido();
        item1.setId(1L);

        ItemPedido item2 = new ItemPedido();
        item2.setId(1L);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1).hasSameHashCodeAs(item2);
    }

    @Test
    void deveSerDiferente_quandoIdsForemDistintos() {
        ItemPedido item1 = new ItemPedido();
        item1.setId(1L);

        ItemPedido item2 = new ItemPedido();
        item2.setId(2L);

        assertThat(item1).isNotEqualTo(item2);
    }
}

package com.elienai.springfood.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.elienai.springfood.domain.exception.NegocioException;

class PedidoTest {

    // =====================================================
    // STATUS INICIAL
    // =====================================================

    @Test
    void deveIniciarComStatusCriado() {
        Pedido pedido = new Pedido();

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.CRIADO);
    }

    // =====================================================
    // CÁLCULO DE VALORES
    // =====================================================

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

    // =====================================================
    // TRANSIÇÕES DE STATUS – CENÁRIOS VÁLIDOS
    // =====================================================

    @Test
    void deveConfirmarPedido_quandoStatusForCriado() {
        Pedido pedido = new Pedido();

        pedido.confirmar();

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.CONFIRMADO);

        assertThat(pedido.getDataConfirmacao())
            .isNotNull()
            .isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    void deveEntregarPedido_quandoStatusForConfirmado() {
        Pedido pedido = new Pedido();
        pedido.confirmar();

        pedido.entregar();

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.ENTREGUE);

        assertThat(pedido.getDataEntrega())
            .isNotNull()
            .isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    void deveCancelarPedido_quandoStatusForCriado() {
        Pedido pedido = new Pedido();

        pedido.cancelar();

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.CANCELADO);

        assertThat(pedido.getDataCancelamento())
            .isNotNull()
            .isBeforeOrEqualTo(OffsetDateTime.now());
    }

    // =====================================================
    // TRANSIÇÕES DE STATUS – CENÁRIOS INVÁLIDOS
    // =====================================================

    @Test
    void naoDevePermitirConfirmarPedido_queJaFoiConfirmado() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCodigo("ABC123");
        pedido.confirmar();

        assertThatThrownBy(pedido::confirmar)
            .isInstanceOf(NegocioException.class)
            .hasMessage("Status do pedido ABC123 não pode ser alterado de Confirmado para Confirmado");
    }

    @Test
    void naoDevePermitirEntregarPedido_queNaoEstejaConfirmado() {
        Pedido pedido = new Pedido();
        pedido.setCodigo("ABC123");

        assertThatThrownBy(pedido::entregar)
            .isInstanceOf(NegocioException.class)
            .hasMessage("Status do pedido ABC123 não pode ser alterado de Criado para Entregue");
    }

    @Test
    void naoDevePermitirCancelarPedido_queJaFoiEntregue() {
        Pedido pedido = new Pedido();
        pedido.setCodigo("ABC123");
        
        pedido.confirmar();
        pedido.entregar();

        assertThatThrownBy(pedido::cancelar)
            .isInstanceOf(NegocioException.class)
            .hasMessage("Status do pedido ABC123 não pode ser alterado de Entregue para Cancelado");
    }

    // =====================================================
    // EQUALS & HASHCODE
    // =====================================================

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

    // =====================================================
    // MÉTODO AUXILIAR
    // =====================================================

    private ItemPedido criarItem(BigDecimal precoUnitario, Integer quantidade) {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(precoUnitario);
        item.setQuantidade(quantidade);
        return item;
    }
}

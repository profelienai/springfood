package com.elienai.springfood.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.StatusPedidoEnum;


@ExtendWith(MockitoExtension.class)
class FluxoPedidoServiceTest {

    @InjectMocks
    private FluxoPedidoService fluxoPedidoService;

    @Mock
    private EmissaoPedidoService emissaoPedidoService;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L); // permitido (id não é regra de negócio)
    }

    // =====================================================
    // CONFIRMAR
    // =====================================================

    @Test
    void deveConfirmarPedido_quandoStatusCriado() {
        when(emissaoPedidoService.buscarOuFalhar("ABC123")).thenReturn(pedido);

        fluxoPedidoService.confirmar("ABC123");

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.CONFIRMADO);

        assertThat(pedido.getDataConfirmacao())
            .isNotNull();
    }

    @Test
    void deveLancarExcecao_quandoConfirmarPedidoJaConfirmado() {
        pedido.confirmar(); // muda estado de forma válida

        when(emissaoPedidoService.buscarOuFalhar("ABC123")).thenReturn(pedido);

        assertThatThrownBy(() -> fluxoPedidoService.confirmar("ABC123"))
            .isInstanceOf(NegocioException.class);
    }

    // =====================================================
    // CANCELAR
    // =====================================================

    @Test
    void deveCancelarPedido_quandoStatusCriado() {
        when(emissaoPedidoService.buscarOuFalhar("ABC123")).thenReturn(pedido);

        fluxoPedidoService.cancelar("ABC123");

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.CANCELADO);

        assertThat(pedido.getDataCancelamento())
            .isNotNull();
    }

    @Test
    void deveLancarExcecao_quandoCancelarPedidoConfirmado() {
        pedido.confirmar();

        when(emissaoPedidoService.buscarOuFalhar("ABC123")).thenReturn(pedido);

        assertThatThrownBy(() -> fluxoPedidoService.cancelar("ABC123"))
            .isInstanceOf(NegocioException.class);
    }

    // =====================================================
    // ENTREGAR
    // =====================================================

    @Test
    void deveEntregarPedido_quandoStatusConfirmado() {
        pedido.confirmar();

        when(emissaoPedidoService.buscarOuFalhar("ABC123")).thenReturn(pedido);

        fluxoPedidoService.entregar("ABC123");

        assertThat(pedido.getStatus())
            .isEqualTo(StatusPedidoEnum.ENTREGUE);

        assertThat(pedido.getDataEntrega())
            .isNotNull();
    }

    @Test
    void deveLancarExcecao_quandoEntregarPedidoCriado() {
        // pedido ainda está CRIADO

        when(emissaoPedidoService.buscarOuFalhar("ABC123")).thenReturn(pedido);

        assertThatThrownBy(() -> fluxoPedidoService.entregar("ABC123"))
            .isInstanceOf(NegocioException.class);
    }
}
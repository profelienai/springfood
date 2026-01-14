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
		pedido.setId(1L);
	}
	
	@Test
	void deveConfirmarPedido_QuandoStatusCriado() {
		pedido.setStatus(StatusPedidoEnum.CRIADO);
		when(emissaoPedidoService.buscarOuFalhar(1L)).thenReturn(pedido);

		fluxoPedidoService.confirmar(1L);

		assertThat(pedido.getStatus()).isEqualTo(StatusPedidoEnum.CONFIRMADO);
		assertThat(pedido.getDataConfirmacao()).isNotNull();
	}
	
	@Test
	void deveLancarExcecao_QuandoConfirmarPedidoComStatusDiferenteDeCriado() {
		pedido.setStatus(StatusPedidoEnum.CONFIRMADO);
		when(emissaoPedidoService.buscarOuFalhar(1L)).thenReturn(pedido);

		assertThatThrownBy(() -> fluxoPedidoService.confirmar(1L))
			.isInstanceOf(NegocioException.class)
			.hasMessage("Status do pedido 1 não pode ser alterado de Confirmado para Confirmado");
	}

	@Test
	void deveCancelarPedido_QuandoStatusCriado() {
		pedido.setStatus(StatusPedidoEnum.CRIADO);
		when(emissaoPedidoService.buscarOuFalhar(1L)).thenReturn(pedido);

		fluxoPedidoService.cancelar(1L);

		assertThat(pedido.getStatus()).isEqualTo(StatusPedidoEnum.CANCELADO);
		assertThat(pedido.getDataCancelamento()).isNotNull();
	}

	@Test
	void deveLancarExcecao_QuandoCancelarPedidoComStatusDiferenteDeCriado() {
		pedido.setStatus(StatusPedidoEnum.CONFIRMADO);
		when(emissaoPedidoService.buscarOuFalhar(1L)).thenReturn(pedido);

		assertThatThrownBy(() -> fluxoPedidoService.cancelar(1L))
			.isInstanceOf(NegocioException.class)
			.hasMessage("Status do pedido 1 não pode ser alterado de Confirmado para Cancelado");
	}

	@Test
	void deveEntregarPedido_QuandoStatusConfirmado() {
		pedido.setStatus(StatusPedidoEnum.CONFIRMADO);
		when(emissaoPedidoService.buscarOuFalhar(1L)).thenReturn(pedido);

		fluxoPedidoService.entregar(1L);

		assertThat(pedido.getStatus()).isEqualTo(StatusPedidoEnum.ENTREGUE);
		assertThat(pedido.getDataEntrega()).isNotNull();
	}

	@Test
	void deveLancarExcecao_QuandoEntregarPedidoComStatusDiferenteDeConfirmado() {
		pedido.setStatus(StatusPedidoEnum.CRIADO);
		when(emissaoPedidoService.buscarOuFalhar(1L)).thenReturn(pedido);

		assertThatThrownBy(() -> fluxoPedidoService.entregar(1L))
			.isInstanceOf(NegocioException.class)
			.hasMessage("Status do pedido 1 não pode ser alterado de Criado para Entregue");
	}
	
}
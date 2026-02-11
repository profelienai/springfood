package com.elienai.springfood.api.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.service.FluxoPedidoService;

@WebMvcTest(FluxoPedidoController.class)
class FluxoPedidoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FluxoPedidoService fluxoPedido;

	// =====================================================
	// CONFIRMAÇÃO
	// =====================================================

	@Test
	void deveRetornarStatus204_QuandoConfirmarPedido() throws Exception {
		doNothing().when(fluxoPedido).confirmar("ABC123");

		mockMvc.perform(put("/pedidos/{codigoPedido}/confirmacao", "ABC123"))
		       .andExpect(status().isNoContent());

		verify(fluxoPedido).confirmar("ABC123");
	}

	@Test
	void deveRetornarStatus400_QuandoConfirmarPedidoComErroDeNegocio() throws Exception {
		doThrow(new NegocioException("Erro de negócio"))
			.when(fluxoPedido).confirmar("ABC123");

		mockMvc.perform(put("/pedidos/{codigoPedido}/confirmacao", "ABC123"))
		       .andExpect(status().isBadRequest());
	}

	// =====================================================
	// CANCELAMENTO
	// =====================================================

	@Test
	void deveRetornarStatus204_QuandoCancelarPedido() throws Exception {
		doNothing().when(fluxoPedido).cancelar("ABC123");

		mockMvc.perform(put("/pedidos/{codigoPedido}/cancelamento", "ABC123"))
		       .andExpect(status().isNoContent());

		verify(fluxoPedido).cancelar("ABC123");
	}

	@Test
	void deveRetornarStatus400_QuandoCancelarPedidoComErroDeNegocio() throws Exception {
		doThrow(new NegocioException("Erro de negócio"))
			.when(fluxoPedido).cancelar("ABC123");

		mockMvc.perform(put("/pedidos/{codigoPedido}/cancelamento", "ABC123"))
		       .andExpect(status().isBadRequest());
	}

	// =====================================================
	// ENTREGA
	// =====================================================

	@Test
	void deveRetornarStatus204_QuandoEntregarPedido() throws Exception {
		doNothing().when(fluxoPedido).entregar("ABC123");

		mockMvc.perform(put("/pedidos/{codigoPedido}/entrega", "ABC123"))
		       .andExpect(status().isNoContent());

		verify(fluxoPedido).entregar("ABC123");
	}

	@Test
	void deveRetornarStatus400_QuandoEntregarPedidoComErroDeNegocio() throws Exception {
		doThrow(new NegocioException("Erro de negócio"))
			.when(fluxoPedido).entregar("ABC123");

		mockMvc.perform(put("/pedidos/{codigoPedido}/entrega", "ABC123"))
		       .andExpect(status().isBadRequest());
	}
}

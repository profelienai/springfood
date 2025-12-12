package com.elienai.springfood.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.api.mapper.FormaPagamentoResponseMapper;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.service.CadastroFormaPagamentoService;
import com.elienai.springfood.domain.service.CadastroRestauranteService;

@WebMvcTest(RestauranteFormaPagamentoController.class)
public class RestauranteFormaPagamentoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CadastroRestauranteService cadastroRestaurante;
	
	@MockBean
	private CadastroFormaPagamentoService cadastroFormaPagamento;
	
	@MockBean
	private FormaPagamentoResponseMapper formaPagamentoResponseMapper;

	private FormaPagamento formaPagamentoCC;
	private FormaPagamento formaPagamentoCD;

	private FormaPagamentoResponse formaPagamentoResponseCC;
	private FormaPagamentoResponse formaPagamentoResponseCD;
	
	private Restaurante restaurante;
	
	@BeforeEach
	private void setUp() {
		prepararDados();
	}
	
	@Test
	public void deveListarFormasPagamento() throws Exception {
		var formasPagto = Set.of(formaPagamentoCC, formaPagamentoCD);
		var responses = List.of(formaPagamentoResponseCC, formaPagamentoResponseCD);
		
		restaurante.getFormasPagamento().addAll(formasPagto);
		
		when(cadastroRestaurante.buscarOuFalhar(1L)).thenReturn(restaurante);
		when(formaPagamentoResponseMapper.toCollectionResponse(formasPagto)).thenReturn(responses);
		
		mockMvc.perform(get("/restaurantes/{restauranteId}/formas-pagamento", 1L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   
			   .andExpect(jsonPath("$[0].id", is(1)))
			   .andExpect(jsonPath("$[0].descricao", is("Cartão de Crédito")))
			   
			   .andExpect(jsonPath("$[1].id", is(2)))
			   .andExpect(jsonPath("$[1].descricao", is("Cartão de Débito")));
		
        verify(cadastroRestaurante).buscarOuFalhar(1L);
        verify(formaPagamentoResponseMapper).toCollectionResponse(formasPagto);
 		
	}
	
	@Test
	public void deveAssociarFormaPagamento() throws Exception {
		mockMvc.perform(put("/restaurantes/{restauranteId}/formas-pagamento/{formaPagamentoId}", 1L, 3L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
        verify(cadastroRestaurante).associarFormaPagamento(1L, 3L);		
	}
	
	@Test
	public void deveDesassociarFormaPagamento() throws Exception {
		mockMvc.perform(delete("/restaurantes/{restauranteId}/formas-pagamento/{formaPagamentoId}", 1L, 3L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
        verify(cadastroRestaurante).desassociarFormaPagamento(1L, 3L);	
	}	

	private void prepararDados() {
		restaurante = new Restaurante();
		restaurante.setId(1L);
		
		formaPagamentoCC = new FormaPagamento();
		formaPagamentoCC.setId(1L);
		formaPagamentoCC.setDescricao("Cartão de Crédito");

		formaPagamentoCD = new FormaPagamento();
		formaPagamentoCD.setId(2L);
		formaPagamentoCD.setDescricao("Cartão de Débito");
		
		formaPagamentoResponseCC = new FormaPagamentoResponse();
		formaPagamentoResponseCC.setId(1L);
		formaPagamentoResponseCC.setDescricao("Cartão de Crédito");
		
		formaPagamentoResponseCD = new FormaPagamentoResponse();
		formaPagamentoResponseCD.setId(2L);
		formaPagamentoResponseCD.setDescricao("Cartão de Débito");
	}
}

package com.elienai.springfood.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.api.dto.FormaPagamentoRequest;
import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.api.mapper.FormaPagamentoRequestMapper;
import com.elienai.springfood.api.mapper.FormaPagamentoResponseMapper;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.repository.FormaPagamentoRepository;
import com.elienai.springfood.domain.service.CadastroFormaPagamentoService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(FormaPagamentoController.class)
public class FormaPagamentoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@MockBean
	private CadastroFormaPagamentoService cadastroFormaPagamento;
	
	@MockBean
	private FormaPagamentoResponseMapper formaPagamentoResponseMapper;

	@MockBean
	private FormaPagamentoRequestMapper formaPagamentoRequestMapper;
	
	private FormaPagamento formaPagamentoCC;
	private FormaPagamento formaPagamentoCD;
	private FormaPagamento formaPagamentoDinheiro;

	private FormaPagamentoResponse formaPagamentoResponseCC;
	private FormaPagamentoResponse formaPagamentoResponseCD;
	private FormaPagamentoResponse formaPagamentoResponseDinheiro;
	
	private String jsonFormaPagamentoDinheiro;
	
	@BeforeEach
	private void setUp() {
		jsonFormaPagamentoDinheiro = ResourceUtils.getContentFromResource(
				"/json/correto/forma-pagamento-dinheiro.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarFormasPagamento() throws Exception {
		List<FormaPagamento> formasPagto = Arrays.asList(formaPagamentoCC, formaPagamentoCD);
		List<FormaPagamentoResponse> responses = Arrays.asList(formaPagamentoResponseCC, formaPagamentoResponseCD);
		
		when(formaPagamentoRepository.findAll()).thenReturn(formasPagto);
		when(formaPagamentoResponseMapper.toCollectionResponse(formasPagto)).thenReturn(responses);
		
		mockMvc.perform(get("/formas-pagamento")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].descricao", is("Cartão de Crédito")))
			   .andExpect(jsonPath("$[1].descricao", is("Cartão de Débito")));
	}
	
	@Test
	public void deveBuscarFormaPagamentoPorId() throws Exception {
		when(cadastroFormaPagamento.buscarOuFalhar(1L)).thenReturn(formaPagamentoCC);
		when(formaPagamentoResponseMapper.toResponse(formaPagamentoCC)).thenReturn(formaPagamentoResponseCC);
		
		mockMvc.perform(get("/formas-pagamento/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.descricao", is("Cartão de Crédito")));
	}
	
	@Test
	public void deveAdicionarFormaPagamento() throws Exception {
		when(formaPagamentoRequestMapper.toDomainObject(any(FormaPagamentoRequest.class))).thenReturn(formaPagamentoDinheiro);
		when(cadastroFormaPagamento.salvar(formaPagamentoDinheiro)).thenReturn(formaPagamentoDinheiro);
		when(formaPagamentoResponseMapper.toResponse(formaPagamentoDinheiro)).thenReturn(formaPagamentoResponseDinheiro);
		
		mockMvc.perform(post("/formas-pagamento")
				.content(jsonFormaPagamentoDinheiro)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.descricao", is("Dinheiro")));
	}
	
	@Test
	public void deveAtualizarFormaPagamento() throws Exception {
		when(cadastroFormaPagamento.buscarOuFalhar(1L)).thenReturn(formaPagamentoDinheiro);
		doNothing().when(formaPagamentoRequestMapper).copyToDomainObject(any(FormaPagamentoRequest.class), any(FormaPagamento.class));
		when(cadastroFormaPagamento.salvar(formaPagamentoDinheiro)).thenReturn(formaPagamentoDinheiro);
		when(formaPagamentoResponseMapper.toResponse(formaPagamentoDinheiro)).thenReturn(formaPagamentoResponseDinheiro);
		
		mockMvc.perform(put("/formas-pagamento/{id}", 1L)
				.content(jsonFormaPagamentoDinheiro)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.descricao", is("Dinheiro")));
	}
	
	@Test
	public void deveRemoverFormaPagamento() throws Exception {
		mockMvc.perform(delete("/formas-pagamento/{id}", 1L))
			.andExpect(status().isNoContent());

		verify(cadastroFormaPagamento).excluir(1L);
	}
	
	private void prepararDados() {
		formaPagamentoCC = new FormaPagamento();
		formaPagamentoCC.setId(1L);
		formaPagamentoCC.setDescricao("Cartão de Crédito");

		formaPagamentoCD = new FormaPagamento();
		formaPagamentoCD.setId(2L);
		formaPagamentoCD.setDescricao("Cartão de Débito");
		
		formaPagamentoDinheiro = new FormaPagamento();
		formaPagamentoDinheiro.setId(1L);
		formaPagamentoDinheiro.setDescricao("Dinheiro");
		
		
		formaPagamentoResponseCC = new FormaPagamentoResponse();
		formaPagamentoResponseCC.setId(1L);
		formaPagamentoResponseCC.setDescricao("Cartão de Crédito");
		
		formaPagamentoResponseCD = new FormaPagamentoResponse();
		formaPagamentoResponseCD.setId(2L);
		formaPagamentoResponseCD.setDescricao("Cartão de Débito");
		
		formaPagamentoResponseDinheiro = new FormaPagamentoResponse();
		formaPagamentoResponseDinheiro.setId(1L);
		formaPagamentoResponseDinheiro.setDescricao("Dinheiro");		
	}
}

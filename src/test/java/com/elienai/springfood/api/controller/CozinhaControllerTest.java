package com.elienai.springfood.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.repository.CozinhaRepository;
import com.elienai.springfood.domain.service.CadastroCozinhaService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(CozinhaController.class)
public class CozinhaControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CozinhaRepository cozinhaRepository;
	
	@MockBean
	private CadastroCozinhaService cadastroCozinha;

	private Cozinha cozinhaItaliana;
	private Cozinha cozinhaArabe;
	private Cozinha cozinhaBrasileira;
	
	private String jsonCozinhaBrasileira;
	
	@BeforeEach
	private void setUp() {
		jsonCozinhaBrasileira = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-brasileira.json");		
		prepararDados();
	}
	
	@Test
	public void deveConterTodosOsEndpoints() throws Exception {
		mockMvc.perform(get("/cozinhas"));
	}
	
	@Test
	public void deveListarCozinhas() throws Exception {
		when(cozinhaRepository.findAll())
			.thenReturn(Arrays.asList(cozinhaItaliana, cozinhaArabe));
		
		mockMvc.perform(get("/cozinhas")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Italiana")))
			   .andExpect(jsonPath("$[1].nome", is("Árabe")));
	}
	
	@Test
	public void deveBuscarCozinhaPorId() throws Exception {
		when(cadastroCozinha.buscarOuFalhar(1L))
			.thenReturn(cozinhaItaliana);
		
		mockMvc.perform(get("/cozinhas/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Italiana")));
	}
	
	@Test
	public void deveAdicionarCozinha() throws Exception {
		when(cadastroCozinha.salvar(any(Cozinha.class)))
			.thenReturn(cozinhaBrasileira);
		
		mockMvc.perform(post("/cozinhas")
				.content(jsonCozinhaBrasileira)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("Brasileira")));
	}
	
	@Test
	public void deveAtualizarCozinha() throws Exception {
		when(cadastroCozinha.buscarOuFalhar(1L)).thenReturn(cozinhaItaliana);
		when(cadastroCozinha.salvar(any(Cozinha.class))).thenReturn(cozinhaBrasileira);
		
		mockMvc.perform(put("/cozinhas/{id}", 1L)
				.content(jsonCozinhaBrasileira)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Brasileira")));
	}
	
	@Test
	public void deveRemoverCozinha() throws Exception {
		mockMvc.perform(delete("/cozinhas/{id}", 1L))
			.andExpect(status().isNoContent());

		verify(cadastroCozinha).excluir(1L);
	}
	
	private void prepararDados() {
		cozinhaItaliana = new Cozinha();
		cozinhaItaliana.setId(1L);
		cozinhaItaliana.setNome("Italiana");

		cozinhaArabe = new Cozinha();
		cozinhaArabe.setId(2L);
		cozinhaArabe.setNome("Árabe");
		
		cozinhaBrasileira = new Cozinha();
		cozinhaBrasileira.setId(1L);
		cozinhaBrasileira.setNome("Brasileira");		
	}
}

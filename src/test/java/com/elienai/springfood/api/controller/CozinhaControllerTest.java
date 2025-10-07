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

import com.elienai.springfood.api.dto.CozinhaRequest;
import com.elienai.springfood.api.dto.CozinhaResponse;
import com.elienai.springfood.api.mapper.CozinhaRequestMapper;
import com.elienai.springfood.api.mapper.CozinhaResponseMapper;
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

	@MockBean
	private CozinhaResponseMapper cozinhaResponseMapper;

	@MockBean
	private CozinhaRequestMapper cozinhaRequestMapper;
	
	private Cozinha cozinhaItaliana;
	private Cozinha cozinhaArabe;
	private Cozinha cozinhaBrasileira;
	
	private CozinhaResponse cozinhaItalianaResponse;
	private CozinhaResponse cozinhaArabeResponse;
	private CozinhaResponse cozinhaBrasileiraResponse;
	
	private String jsonCozinhaBrasileira;
	
	@BeforeEach
	private void setUp() {
		jsonCozinhaBrasileira = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-brasileira.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarCozinhas() throws Exception {
		List<Cozinha> cozinhas = Arrays.asList(cozinhaItaliana, cozinhaArabe);
		List<CozinhaResponse> responses = Arrays.asList(cozinhaItalianaResponse, cozinhaArabeResponse);
		
		when(cozinhaRepository.findAll()).thenReturn(cozinhas);
		when(cozinhaResponseMapper.toCollectionResponse(cozinhas)).thenReturn(responses);
		
		mockMvc.perform(get("/cozinhas")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Italiana")))
			   .andExpect(jsonPath("$[1].nome", is("Árabe")));
	}
	
	@Test
	public void deveBuscarCozinhaPorId() throws Exception {
		when(cadastroCozinha.buscarOuFalhar(1L)).thenReturn(cozinhaItaliana);
		when(cozinhaResponseMapper.toResponse(cozinhaItaliana)).thenReturn(cozinhaItalianaResponse);
		
		mockMvc.perform(get("/cozinhas/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Italiana")));
	}
	
	@Test
	public void deveAdicionarCozinha() throws Exception {
		when(cozinhaRequestMapper.toDomainObject(any(CozinhaRequest.class))).thenReturn(cozinhaBrasileira);
		when(cadastroCozinha.salvar(any(Cozinha.class))).thenReturn(cozinhaBrasileira);
		when(cozinhaResponseMapper.toResponse(cozinhaBrasileira)).thenReturn(cozinhaBrasileiraResponse);
		
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
		doNothing().when(cozinhaRequestMapper).copyToDomainObject(any(CozinhaRequest.class), any(Cozinha.class));
		when(cadastroCozinha.salvar(cozinhaBrasileira)).thenReturn(cozinhaBrasileira);
		when(cozinhaResponseMapper.toResponse(cozinhaBrasileira)).thenReturn(cozinhaBrasileiraResponse);
		
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
		
		cozinhaItalianaResponse = new CozinhaResponse();
		cozinhaItalianaResponse.setId(1L);
		cozinhaItalianaResponse.setNome("Italiana");

		cozinhaArabeResponse = new CozinhaResponse();
		cozinhaArabeResponse.setId(2L);
		cozinhaArabeResponse.setNome("Árabe");
		
		cozinhaBrasileiraResponse = new CozinhaResponse();
		cozinhaBrasileiraResponse.setId(1L);
		cozinhaBrasileiraResponse.setNome("Brasileira");
	}
}

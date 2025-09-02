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

import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.CidadeRepository;
import com.elienai.springfood.domain.service.CadastroCidadeService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(CidadeController.class)
public class CidadeControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CidadeRepository cidadeRepository;
	
	@MockBean
	private CadastroCidadeService cadastroCidade;

	private Cidade cidadeBH;	
	private Cidade cidadeRJ;
	private Cidade cidadeVR;
	
	private String jsonCidadeVR;
	
	@BeforeEach
	private void setUp() {
		jsonCidadeVR = ResourceUtils.getContentFromResource(
				"/json/correto/cidade-vr.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarCidades() throws Exception {
		when(cidadeRepository.findAll())
			.thenReturn(Arrays.asList(cidadeBH, cidadeRJ));
		
		mockMvc.perform(get("/cidades")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Belo Horizonte")))
			   .andExpect(jsonPath("$[0].estado.nome", is("Minas Gerais")))
			   .andExpect(jsonPath("$[1].nome", is("Rio de Janeiro")))
			   .andExpect(jsonPath("$[1].estado.nome", is("Rio de Janeiro")));
	}
	
	@Test
	public void deveBuscarCidadePorId() throws Exception {
		when(cadastroCidade.buscarOuFalhar(1L))
			.thenReturn(cidadeRJ);
		
		mockMvc.perform(get("/cidades/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Rio de Janeiro")))
		    .andExpect(jsonPath("$.estado.nome", is("Rio de Janeiro")));
	}
	
	@Test
	public void deveAdicionarCidade() throws Exception {
		when(cadastroCidade.salvar(any(Cidade.class)))
			.thenReturn(cidadeVR);
		
		mockMvc.perform(post("/cidades")
				.content(jsonCidadeVR)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("Volta Redonda")))
		    .andExpect(jsonPath("$.estado.nome", is("Rio de Janeiro")));
	}
	
	@Test
	public void deveAtualizarCidade() throws Exception {
		when(cadastroCidade.buscarOuFalhar(1L)).thenReturn(cidadeRJ);
		when(cadastroCidade.salvar(any(Cidade.class))).thenReturn(cidadeVR);
		
		mockMvc.perform(put("/cidades/{id}", 1L)
				.content(jsonCidadeVR)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Volta Redonda")))
			.andExpect(jsonPath("$.estado.nome", is("Rio de Janeiro")));
	}
	
	@Test
	public void deveRemoverCidade() throws Exception {
		mockMvc.perform(delete("/cidades/{id}", 1L))
			.andExpect(status().isNoContent());

		verify(cadastroCidade).excluir(1L);
	}
	
	private void prepararDados() {
		Estado estadoRJ = new Estado();
		estadoRJ.setId(1L);
		estadoRJ.setNome("Rio de Janeiro");

		Estado estadoMG = new Estado();
		estadoMG.setId(2L);
		estadoMG.setNome("Minas Gerais");		
		
		cidadeBH = new Cidade();
		cidadeBH.setId(1L);
		cidadeBH.setNome("Belo Horizonte");
		cidadeBH.setEstado(estadoMG);

		cidadeRJ = new Cidade();
		cidadeRJ.setId(2L);
		cidadeRJ.setNome("Rio de Janeiro");
		cidadeRJ.setEstado(estadoRJ);
		
		cidadeVR = new Cidade();
		cidadeVR.setId(1L);
		cidadeVR.setNome("Volta Redonda");
		cidadeVR.setEstado(estadoRJ);
	}
}

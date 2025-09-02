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

import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.EstadoRepository;
import com.elienai.springfood.domain.service.CadastroEstadoService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(EstadoController.class)
public class EstadoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EstadoRepository estadoRepository;
	
	@MockBean
	private CadastroEstadoService cadastroEstado;

	private Estado estadoRJ;
	private Estado estadoMG;
	private Estado estadoSP;
	
	private String jsonEstadoSP;
	
	@BeforeEach
	private void setUp() {
		jsonEstadoSP = ResourceUtils.getContentFromResource(
				"/json/correto/estado-sp.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarEstados() throws Exception {
		when(estadoRepository.findAll())
			.thenReturn(Arrays.asList(estadoRJ, estadoMG));
		
		mockMvc.perform(get("/estados")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Rio de Janeiro")))
			   .andExpect(jsonPath("$[1].nome", is("Minas Gerais")));
	}
	
	@Test
	public void deveBuscarEstadoPorId() throws Exception {
		when(cadastroEstado.buscarOuFalhar(1L))
			.thenReturn(estadoRJ);
		
		mockMvc.perform(get("/estados/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Rio de Janeiro")));
	}
	
	@Test
	public void deveAdicionarEstado() throws Exception {
		when(cadastroEstado.salvar(any(Estado.class)))
			.thenReturn(estadoSP);
		
		mockMvc.perform(post("/estados")
				.content(jsonEstadoSP)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("São Paulo")));
	}
	
	@Test
	public void deveAtualizarEstado() throws Exception {
		when(cadastroEstado.buscarOuFalhar(1L)).thenReturn(estadoRJ);
		when(cadastroEstado.salvar(any(Estado.class))).thenReturn(estadoSP);
		
		mockMvc.perform(put("/estados/{id}", 1L)
				.content(jsonEstadoSP)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("São Paulo")));
	}
	
	@Test
	public void deveRemoverEstado() throws Exception {
		mockMvc.perform(delete("/estados/{id}", 1L))
			.andExpect(status().isNoContent());

		verify(cadastroEstado).excluir(1L);
	}
	
	private void prepararDados() {
		estadoRJ = new Estado();
		estadoRJ.setId(1L);
		estadoRJ.setNome("Rio de Janeiro");

		estadoMG = new Estado();
		estadoMG.setId(2L);
		estadoMG.setNome("Minas Gerais");
		
		estadoSP = new Estado();
		estadoSP.setId(1L);
		estadoSP.setNome("São Paulo");		
	}
}

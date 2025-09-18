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

import com.elienai.springfood.api.dto.EstadoRequest;
import com.elienai.springfood.api.dto.EstadoResponse;
import com.elienai.springfood.api.mapper.EstadoRequestMapper;
import com.elienai.springfood.api.mapper.EstadoResponseMapper;
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
	
	@MockBean
	private EstadoResponseMapper estadoResponseMapper;

	@MockBean
	private EstadoRequestMapper estadoRequestMapper;
	
	private Estado estadoRJ;
	private Estado estadoMG;
	private Estado estadoSP;

	private EstadoResponse estadoResponseRJ;
	private EstadoResponse estadoResponseMG;
	private EstadoResponse estadoResponseSP;
	
	private String jsonEstadoSP;
	
	@BeforeEach
	private void setUp() {
		jsonEstadoSP = ResourceUtils.getContentFromResource(
				"/json/correto/estado-sp.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarEstados() throws Exception {
		List<Estado> estados = Arrays.asList(estadoRJ, estadoMG);
		List<EstadoResponse> responses = Arrays.asList(estadoResponseRJ, estadoResponseMG);
		
		when(estadoRepository.findAll()).thenReturn(estados);
		when(estadoResponseMapper.toCollectionResponse(estados)).thenReturn(responses);
		
		mockMvc.perform(get("/estados")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Rio de Janeiro")))
			   .andExpect(jsonPath("$[1].nome", is("Minas Gerais")));
	}
	
	@Test
	public void deveBuscarEstadoPorId() throws Exception {
		when(cadastroEstado.buscarOuFalhar(1L)).thenReturn(estadoRJ);
		when(estadoResponseMapper.toResponse(estadoRJ)).thenReturn(estadoResponseRJ);
		
		mockMvc.perform(get("/estados/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Rio de Janeiro")));
	}
	
	@Test
	public void deveAdicionarEstado() throws Exception {
		when(estadoRequestMapper.toDomainObject(any(EstadoRequest.class))).thenReturn(estadoSP);
		when(cadastroEstado.salvar(estadoSP)).thenReturn(estadoSP);
		when(estadoResponseMapper.toResponse(estadoSP)).thenReturn(estadoResponseSP);
		
		mockMvc.perform(post("/estados")
				.content(jsonEstadoSP)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("São Paulo")));
	}
	
	@Test
	public void deveAtualizarEstado() throws Exception {
		when(cadastroEstado.buscarOuFalhar(1L)).thenReturn(estadoSP);
		doNothing().when(estadoRequestMapper).copyToDomainObject(any(EstadoRequest.class), any(Estado.class));
		when(cadastroEstado.salvar(estadoSP)).thenReturn(estadoSP);
		when(estadoResponseMapper.toResponse(estadoSP)).thenReturn(estadoResponseSP);
		
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
		
		
		estadoResponseRJ = new EstadoResponse();
		estadoResponseRJ.setId(1L);
		estadoResponseRJ.setNome("Rio de Janeiro");
		
		estadoResponseMG = new EstadoResponse();
		estadoResponseMG.setId(2L);
		estadoResponseMG.setNome("Minas Gerais");
		
		estadoResponseSP = new EstadoResponse();
		estadoResponseSP.setId(1L);
		estadoResponseSP.setNome("São Paulo");		
	}
}

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

import com.elienai.springfood.api.dto.GrupoRequest;
import com.elienai.springfood.api.dto.GrupoResponse;
import com.elienai.springfood.api.mapper.GrupoRequestMapper;
import com.elienai.springfood.api.mapper.GrupoResponseMapper;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.repository.GrupoRepository;
import com.elienai.springfood.domain.service.CadastroGrupoService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(GrupoController.class)
public class GrupoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private GrupoRepository grupoRepository;
	
	@MockBean
	private CadastroGrupoService cadastroGrupo;
	
	@MockBean
	private GrupoResponseMapper grupoResponseMapper;

	@MockBean
	private GrupoRequestMapper grupoRequestMapper;
	
	private Grupo grupoVendedor;
	private Grupo grupoGerente;
	private Grupo grupoSecretaria;

	private GrupoResponse grupoResponseVendedor;
	private GrupoResponse grupoResponseGerente;
	private GrupoResponse grupoResponseSecretaria;
	
	private String jsonGrupoSP;
	
	@BeforeEach
	private void setUp() {
		jsonGrupoSP = ResourceUtils.getContentFromResource(
				"/json/correto/grupo-secretaria.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarGrupos() throws Exception {
		List<Grupo> grupos = Arrays.asList(grupoVendedor, grupoGerente);
		List<GrupoResponse> responses = Arrays.asList(grupoResponseVendedor, grupoResponseGerente);
		
		when(grupoRepository.findAll()).thenReturn(grupos);
		when(grupoResponseMapper.toCollectionResponse(grupos)).thenReturn(responses);
		
		mockMvc.perform(get("/grupos")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Vendedor")))
			   .andExpect(jsonPath("$[1].nome", is("Gerente")));
	}
	
	@Test
	public void deveBuscarGrupoPorId() throws Exception {
		when(cadastroGrupo.buscarOuFalhar(1L)).thenReturn(grupoVendedor);
		when(grupoResponseMapper.toResponse(grupoVendedor)).thenReturn(grupoResponseVendedor);
		
		mockMvc.perform(get("/grupos/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Vendedor")));
	}
	
	@Test
	public void deveAdicionarGrupo() throws Exception {
		when(grupoRequestMapper.toDomainObject(any(GrupoRequest.class))).thenReturn(grupoSecretaria);
		when(cadastroGrupo.salvar(grupoSecretaria)).thenReturn(grupoSecretaria);
		when(grupoResponseMapper.toResponse(grupoSecretaria)).thenReturn(grupoResponseSecretaria);
		
		mockMvc.perform(post("/grupos")
				.content(jsonGrupoSP)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("Secretaria")));
	}
	
	@Test
	public void deveAtualizarGrupo() throws Exception {
		when(cadastroGrupo.buscarOuFalhar(1L)).thenReturn(grupoSecretaria);
		doNothing().when(grupoRequestMapper).copyToDomainObject(any(GrupoRequest.class), any(Grupo.class));
		when(cadastroGrupo.salvar(grupoSecretaria)).thenReturn(grupoSecretaria);
		when(grupoResponseMapper.toResponse(grupoSecretaria)).thenReturn(grupoResponseSecretaria);
		
		mockMvc.perform(put("/grupos/{id}", 1L)
				.content(jsonGrupoSP)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Secretaria")));
	}
	
	@Test
	public void deveRemoverGrupo() throws Exception {
		mockMvc.perform(delete("/grupos/{id}", 1L))
			.andExpect(status().isNoContent());

		verify(cadastroGrupo).excluir(1L);
	}
	
	private void prepararDados() {
		grupoVendedor = new Grupo();
		grupoVendedor.setId(1L);
		grupoVendedor.setNome("Vendedor");

		grupoGerente = new Grupo();
		grupoGerente.setId(2L);
		grupoGerente.setNome("Gerente");
		
		grupoSecretaria = new Grupo();
		grupoSecretaria.setId(1L);
		grupoSecretaria.setNome("Secretaria");
		
		
		grupoResponseVendedor = new GrupoResponse();
		grupoResponseVendedor.setId(1L);
		grupoResponseVendedor.setNome("Vendedor");
		
		grupoResponseGerente = new GrupoResponse();
		grupoResponseGerente.setId(2L);
		grupoResponseGerente.setNome("Gerente");
		
		grupoResponseSecretaria = new GrupoResponse();
		grupoResponseSecretaria.setId(1L);
		grupoResponseSecretaria.setNome("Secretaria");		
	}
}

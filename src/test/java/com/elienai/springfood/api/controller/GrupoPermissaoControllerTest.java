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

import com.elienai.springfood.api.dto.PermissaoResponse;
import com.elienai.springfood.api.mapper.PermissaoResponseMapper;
import com.elienai.springfood.domain.model.Permissao;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.service.CadastroPermissaoService;
import com.elienai.springfood.domain.service.CadastroGrupoService;

@WebMvcTest(GrupoPermissaoController.class)
public class GrupoPermissaoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CadastroGrupoService cadastroGrupo;
	
	@MockBean
	private CadastroPermissaoService cadastroPermissao;
	
	@MockBean
	private PermissaoResponseMapper permissaoResponseMapper;

	private Permissao permissaoConsultar;
	private Permissao permissaoEditar;

	private PermissaoResponse permissaoResponseConsultar;
	private PermissaoResponse permissaoResponseEditar;
	
	private Grupo grupo;
	
	@BeforeEach
	private void setUp() {
		prepararDados();
	}
	
	@Test
	public void deveListarPermissoes() throws Exception {
		var formasPagto = Set.of(permissaoConsultar, permissaoEditar);
		var responses = List.of(permissaoResponseConsultar, permissaoResponseEditar);
		
		grupo.getPermissoes().addAll(formasPagto);
		
		when(cadastroGrupo.buscarOuFalhar(1L)).thenReturn(grupo);
		when(permissaoResponseMapper.toCollectionResponse(formasPagto)).thenReturn(responses);
		
		mockMvc.perform(get("/grupos/{grupoId}/permissoes", 1L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   
			   .andExpect(jsonPath("$[0].id", is(1)))
			   .andExpect(jsonPath("$[0].nome", is("CONSULTAR_COZINHAS")))
			   .andExpect(jsonPath("$[0].descricao", is("Permite consultar cozinhas")))
			   
			   .andExpect(jsonPath("$[1].id", is(2)))
			   .andExpect(jsonPath("$[1].nome", is("EDITAR_COZINHAS")))
			   .andExpect(jsonPath("$[1].descricao", is("Permite editar cozinhas")));
		
        verify(cadastroGrupo).buscarOuFalhar(1L);
        verify(permissaoResponseMapper).toCollectionResponse(formasPagto);
 		
	}
	
	@Test
	public void deveAssociarPermissao() throws Exception {
		mockMvc.perform(put("/grupos/{grupoId}/permissoes/{permissaoId}", 1L, 3L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
        verify(cadastroGrupo).associarPermissao(1L, 3L);		
	}
	
	@Test
	public void deveDesassociarPermissao() throws Exception {
		mockMvc.perform(delete("/grupos/{grupoId}/permissoes/{permissaoId}", 1L, 3L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
        verify(cadastroGrupo).desassociarPermissao(1L, 3L);	
	}	

	private void prepararDados() {
		grupo = new Grupo();
		grupo.setId(1L);
		
		permissaoConsultar = new Permissao();
		permissaoConsultar.setId(1L);
		permissaoConsultar.setNome("CONSULTAR_COZINHAS");
		permissaoConsultar.setDescricao("Permite consultar cozinhas");

		permissaoEditar = new Permissao();
		permissaoEditar.setId(2L);
		permissaoEditar.setNome("EDITAR_COZINHAS");
		permissaoEditar.setDescricao("Permite editar cozinhas");
		
		permissaoResponseConsultar = new PermissaoResponse();
		permissaoResponseConsultar.setId(1L);
		permissaoResponseConsultar.setNome("CONSULTAR_COZINHAS");
		permissaoResponseConsultar.setDescricao("Permite consultar cozinhas");
		
		permissaoResponseEditar = new PermissaoResponse();
		permissaoResponseEditar.setId(2L);
		permissaoResponseEditar.setNome("EDITAR_COZINHAS");
		permissaoResponseEditar.setDescricao("Permite editar cozinhas");
	}
}

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

import com.elienai.springfood.api.dto.GrupoResponse;
import com.elienai.springfood.api.mapper.GrupoResponseMapper;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.service.CadastroUsuarioService;

@WebMvcTest(UsuarioGrupoController.class)
public class UsuarioGrupoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CadastroUsuarioService cadastroUsuario;

	@MockBean
	private GrupoResponseMapper grupoResponseMapper;

	private Usuario usuario;

	private Grupo grupoAdmin;
	private Grupo grupoVendas;

	private GrupoResponse grupoResponseAdmin;
	private GrupoResponse grupoResponseVendas;

	@BeforeEach
	void setUp() {
		prepararDados();
	}

	@Test
	public void deveListarGruposDoUsuario() throws Exception {
		var grupos = Set.of(grupoAdmin, grupoVendas);
		var responses = List.of(grupoResponseAdmin, grupoResponseVendas);

		usuario.getGrupos().addAll(grupos);

		when(cadastroUsuario.buscarOuFalhar(1L)).thenReturn(usuario);
		when(grupoResponseMapper.toCollectionResponse(grupos)).thenReturn(responses);

		mockMvc.perform(get("/usuarios/{usuarioId}/grupos", 1L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))

			   .andExpect(jsonPath("$[0].id", is(1)))
			   .andExpect(jsonPath("$[0].nome", is("ADMINISTRADOR")))

			   .andExpect(jsonPath("$[1].id", is(2)))
			   .andExpect(jsonPath("$[1].nome", is("VENDEDOR")));

		verify(cadastroUsuario).buscarOuFalhar(1L);
		verify(grupoResponseMapper).toCollectionResponse(grupos);
	}

	@Test
	public void deveAssociarGrupoAoUsuario() throws Exception {
		mockMvc.perform(put("/usuarios/{usuarioId}/grupos/{grupoId}", 1L, 3L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isNoContent());

		verify(cadastroUsuario).associarGrupo(1L, 3L);
	}

	@Test
	public void deveDesassociarGrupoDoUsuario() throws Exception {
		mockMvc.perform(delete("/usuarios/{usuarioId}/grupos/{grupoId}", 1L, 3L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isNoContent());

		verify(cadastroUsuario).desassociarGrupo(1L, 3L);
	}

	private void prepararDados() {
		usuario = new Usuario();
		usuario.setId(1L);

		grupoAdmin = new Grupo();
		grupoAdmin.setId(1L);
		grupoAdmin.setNome("ADMINISTRADOR");

		grupoVendas = new Grupo();
		grupoVendas.setId(2L);
		grupoVendas.setNome("VENDEDOR");

		grupoResponseAdmin = new GrupoResponse();
		grupoResponseAdmin.setId(1L);
		grupoResponseAdmin.setNome("ADMINISTRADOR");

		grupoResponseVendas = new GrupoResponse();
		grupoResponseVendas.setId(2L);
		grupoResponseVendas.setNome("VENDEDOR");
	}
}

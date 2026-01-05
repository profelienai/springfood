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

import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.api.mapper.UsuarioResponseMapper;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.service.CadastroRestauranteService;

@WebMvcTest(RestauranteUsuarioResponsavelController.class)
public class RestauranteUsuarioResponsavelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CadastroRestauranteService cadastroRestaurante;

	@MockBean
	private UsuarioResponseMapper usuarioResponseMapper;

	private Restaurante restaurante;

	private Usuario usuario1;
	private Usuario usuario2;

	private UsuarioResponse usuarioResponse1;
	private UsuarioResponse usuarioResponse2;

	@BeforeEach
	void setUp() {
		prepararDados();
	}

	@Test
	public void deveListarResponsaveisDoRestaurante() throws Exception {
		var responsaveis = Set.of(usuario1, usuario2);
		var responses = List.of(usuarioResponse1, usuarioResponse2);

		restaurante.getResponsaveis().addAll(responsaveis);

		when(cadastroRestaurante.buscarOuFalhar(1L)).thenReturn(restaurante);
		when(usuarioResponseMapper.toCollectionResponse(responsaveis)).thenReturn(responses);

		mockMvc.perform(get("/restaurantes/{restauranteId}/responsaveis", 1L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))

			   .andExpect(jsonPath("$[0].id", is(1)))
			   .andExpect(jsonPath("$[0].nome", is("João")))

			   .andExpect(jsonPath("$[1].id", is(2)))
			   .andExpect(jsonPath("$[1].nome", is("Maria")));

		verify(cadastroRestaurante).buscarOuFalhar(1L);
		verify(usuarioResponseMapper).toCollectionResponse(responsaveis);
	}

	@Test
	public void deveAssociarResponsavelAoRestaurante() throws Exception {
		mockMvc.perform(put("/restaurantes/{restauranteId}/responsaveis/{usuarioId}", 1L, 3L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isNoContent());

		verify(cadastroRestaurante).associarResponsavel(1L, 3L);
	}

	@Test
	public void deveDesassociarResponsavelDoRestaurante() throws Exception {
		mockMvc.perform(delete("/restaurantes/{restauranteId}/responsaveis/{usuarioId}", 1L, 3L)
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isNoContent());

		verify(cadastroRestaurante).desassociarResponsavel(1L, 3L);
	}

	private void prepararDados() {
		restaurante = new Restaurante();
		restaurante.setId(1L);

		usuario1 = new Usuario();
		usuario1.setId(1L);
		usuario1.setNome("João");

		usuario2 = new Usuario();
		usuario2.setId(2L);
		usuario2.setNome("Maria");

		usuarioResponse1 = new UsuarioResponse();
		usuarioResponse1.setId(1L);
		usuarioResponse1.setNome("João");

		usuarioResponse2 = new UsuarioResponse();
		usuarioResponse2.setId(2L);
		usuarioResponse2.setNome("Maria");
	}
}

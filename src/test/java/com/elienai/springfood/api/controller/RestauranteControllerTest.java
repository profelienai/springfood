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

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.RestauranteRepository;
import com.elienai.springfood.domain.service.CadastroRestauranteService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(RestauranteController.class)
public class RestauranteControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private RestauranteRepository restauranteRepository;
	
	@MockBean
	private CadastroRestauranteService cadastroRestaurante;

	private Restaurante restauranteA;	
	private Restaurante restauranteB;
	private Restaurante restauranteC;
	
	private String jsonRestauranteCadore;
	
	@BeforeEach
	private void setUp() {
		jsonRestauranteCadore = ResourceUtils.getContentFromResource(
				"/json/correto/restaurante-cadore.json");		
		prepararDados();
	}
	
	@Test
	public void deveListarRestaurante() throws Exception {
		when(restauranteRepository.findAll())
			.thenReturn(Arrays.asList(restauranteA, restauranteB));
		
		mockMvc.perform(get("/restaurantes")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   
			   .andExpect(jsonPath("$[0].nome", is("Angelo")))
			   .andExpect(jsonPath("$[0].taxaFrete", is(new BigDecimal(7.99))))
			   .andExpect(jsonPath("$[0].cozinha.nome", is("Italiana")))
			   
			   .andExpect(jsonPath("$[1].nome", is("Bocado")))
			   .andExpect(jsonPath("$[1].taxaFrete", is(new BigDecimal(8.99))))		   
			   .andExpect(jsonPath("$[1].cozinha.nome", is("Brasileira")));
	}
	
	@Test
	public void deveBuscarRestaurantePorId() throws Exception {
		when(cadastroRestaurante.buscarOuFalhar(2L))
			.thenReturn(restauranteB);
		
		mockMvc.perform(get("/restaurantes/{id}", 2L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Bocado")))
			.andExpect(jsonPath("$.taxaFrete", is(new BigDecimal(8.99))))		   
			.andExpect(jsonPath("$.cozinha.nome", is("Brasileira")));
	}
	
	@Test
	public void deveAdicionarRestaurante() throws Exception {
		when(cadastroRestaurante.salvar(any(Restaurante.class)))
			.thenReturn(restauranteC);
		
		mockMvc.perform(post("/restaurantes")
				.content(jsonRestauranteCadore)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("Cadore")))
			.andExpect(jsonPath("$.taxaFrete", is(new BigDecimal(10.99))))		   
			.andExpect(jsonPath("$.cozinha.nome", is("Italiana")));
	}
	
	@Test
	public void deveAtualizarRestaurante() throws Exception {
		when(cadastroRestaurante.buscarOuFalhar(1L)).thenReturn(restauranteA);
		when(cadastroRestaurante.salvar(any(Restaurante.class))).thenReturn(restauranteC);
		
		mockMvc.perform(put("/restaurantes/{id}", 1L)
				.content(jsonRestauranteCadore)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Cadore")))
			.andExpect(jsonPath("$.taxaFrete", is(new BigDecimal(10.99))))		   
			.andExpect(jsonPath("$.cozinha.nome", is("Italiana")));
	}
	
	@Test
	public void deveRemoverRestaurante() throws Exception {
		mockMvc.perform(delete("/restaurantes/{id}", 1L))
			.andExpect(status().isNoContent());

		verify(cadastroRestaurante).excluir(1L);
	}
	
	private void prepararDados() {
		Cozinha cozinhaItaliana = new Cozinha();
		cozinhaItaliana.setId(1L);
		cozinhaItaliana.setNome("Italiana");
		
		Cozinha cozinhaBrasileira = new Cozinha();
		cozinhaBrasileira.setId(2L);
		cozinhaBrasileira.setNome("Brasileira");			
		
		restauranteA = new Restaurante();
		restauranteA.setId(1L);
		restauranteA.setNome("Angelo");
		restauranteA.setTaxaFrete(new BigDecimal(7.99));
		restauranteA.setCozinha(cozinhaItaliana);

		restauranteB = new Restaurante();
		restauranteB.setId(2L);
		restauranteB.setNome("Bocado");
		restauranteB.setTaxaFrete(new BigDecimal(8.99));
		restauranteB.setCozinha(cozinhaBrasileira);
		
		restauranteC = new Restaurante();
		restauranteC.setId(1L);
		restauranteC.setNome("Cadore");
		restauranteC.setTaxaFrete(new BigDecimal(10.99));
		restauranteC.setCozinha(cozinhaItaliana);
	}
}

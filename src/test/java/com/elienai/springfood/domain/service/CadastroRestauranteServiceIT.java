package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.RestauranteNaoEncontradoException;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.RestauranteRepository;
import com.elienai.springfood.util.DatabaseCleaner;

/**
 * Testes de integração para CadastroRestauranteService.
 * 
 * Cobrem:
 *  - Busca por ID
 *  - Inclusão
 *  - Exclusão
 *  - Exceções quando a restaurante não existe
 * 
 * Dados iniciais carregados via @Sql.
*/

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroRestauranteServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroRestauranteServiceIT {

	@Autowired
	private CadastroRestauranteService cadastroRestaurante;
	
	@Autowired
	private RestauranteRepository restauranteRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    /**
     * Limpa o banco após cada teste, garantindo isolamento entre os testes.
     */
    @AfterEach
    private void cleanDatabase() {
    	databaseCleaner.clearTables();
    }
    
	@Test
	public void deveRetornarRestaurantePorId() {
		Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(1L);
		
		assertNotNull(restaurante);
		assertEquals("Aramad", restaurante.getNome());
	}
	
	@Test
	public void deveSalvarRestaurante() {
		Cozinha cozinha = new Cozinha();
		cozinha.setId(1L);
		
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Brasador Steakhouse");
		restaurante.setTaxaFrete(new BigDecimal(10.00d));
		restaurante.setCozinha(cozinha);
		
		Restaurante restauranteSalvo = cadastroRestaurante.salvar(restaurante);

		// Valida o objeto salvo
		assertNotNull(restauranteSalvo);
		assertNotNull(restauranteSalvo.getId());
		assertEquals("Brasador Steakhouse", restauranteSalvo.getNome());
		
		// Confirma persistência no banco via Repository
		Optional<Restaurante> optRestaurante = restauranteRepository.findById(restauranteSalvo.getId());
		assertTrue(optRestaurante.isPresent());
		assertEquals("Brasador Steakhouse", optRestaurante.get().getNome());
	}	
	
	@Test
	public void deveExcluirRestaurante() {
		cadastroRestaurante.excluir(1L);

		// Confirma persistência no banco via Repository
		Optional<Restaurante> optRestaurante = restauranteRepository.findById(1L);
		assertTrue(optRestaurante.isEmpty());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoRestauranteInexistente() {
		RestauranteNaoEncontradoException ex = assertThrows(RestauranteNaoEncontradoException.class, () -> cadastroRestaurante.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de restaurante com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoRestauranteInexistente() {
		RestauranteNaoEncontradoException ex = assertThrows(RestauranteNaoEncontradoException.class, () -> cadastroRestaurante.excluir(99L));
		
		assertEquals("Não existe um cadastro de restaurante com código 99", ex.getMessage());
	}	
}

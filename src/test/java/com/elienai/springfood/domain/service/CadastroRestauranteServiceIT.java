package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.RestauranteNaoEncontradoException;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Endereco;
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
        Cidade cidade = new Cidade();
        cidade.setId(1L);

        Endereco endereco = new Endereco();
        endereco.setCep("38400-999");
        endereco.setLogradouro("Rua João Pinheiro");
        endereco.setNumero("1000");
        endereco.setComplemento("C1");
        endereco.setBairro("Centro");
        endereco.setCidade(cidade);
        
		Cozinha cozinha = new Cozinha();
		cozinha.setId(1L);
		
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Brasador Steakhouse");
		restaurante.setTaxaFrete(new BigDecimal(10.00d));
		restaurante.setCozinha(cozinha);
		restaurante.setEndereco(endereco);
		
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
	
	@Test
	public void deveAtivarRestaurante() {
		Long restauranteId = 2L;

		assertFalse(cadastroRestaurante.buscarOuFalhar(restauranteId).getAtivo());
		cadastroRestaurante.ativar(restauranteId);
		assertTrue(cadastroRestaurante.buscarOuFalhar(restauranteId).getAtivo());
	}
	
	@Test
	public void deveInativarRestaurante() {
		Long restauranteId = 1L;
		
		assertTrue(cadastroRestaurante.buscarOuFalhar(restauranteId).getAtivo());
		cadastroRestaurante.inativar(restauranteId);
		assertFalse(cadastroRestaurante.buscarOuFalhar(restauranteId).getAtivo());
	}
	
	@Test
	public void deveAtivarListaDeRestaurante() {
		List<Long> restaurantes = List.of(1L, 2L);
		
		cadastroRestaurante.ativar(restaurantes);
		
		restaurantes.forEach(
				restauranteId -> assertTrue(cadastroRestaurante.buscarOuFalhar(restauranteId).getAtivo()) );
	}
	
	@Test
	public void deveInativarListaDeRestaurante() {
		List<Long> restaurantes = List.of(1L, 2L);
		
		cadastroRestaurante.inativar(restaurantes);
		
		restaurantes.forEach(
				restauranteId -> assertFalse(cadastroRestaurante.buscarOuFalhar(restauranteId).getAtivo()) );
	}
	
	@Test
	public void deveAbrirRestaurante() {
		Long restauranteId = 2L;

		assertFalse(cadastroRestaurante.buscarOuFalhar(restauranteId).getAberto());
		cadastroRestaurante.abrir(restauranteId);
		assertTrue(cadastroRestaurante.buscarOuFalhar(restauranteId).getAberto());
	}
	
	@Test
	public void deveFecharRestaurante() {
		Long restauranteId = 1L;
		
		assertTrue(cadastroRestaurante.buscarOuFalhar(restauranteId).getAberto());
		cadastroRestaurante.fechar(restauranteId);
		assertFalse(cadastroRestaurante.buscarOuFalhar(restauranteId).getAberto());
	}	
	
	@Test
	public void deveAssociarFormaPagamento() {
	    Long restauranteId = 1L;
	    Long formaPagamentoId = 1L;

	    // Antes: restaurante não possui forma de pagamento
	    Restaurante restauranteAntes = cadastroRestaurante.buscarOuFalhar(restauranteId);
	    assertFalse(restauranteAntes.getFormasPagamento()
	            .stream()
	            .anyMatch(fp -> fp.getId().equals(formaPagamentoId)));

	    // Executa a associação
	    cadastroRestaurante.associarFormaPagamento(restauranteId, formaPagamentoId);

	    // Depois: restaurante deve possuir a forma de pagamento
	    Restaurante restauranteDepois = cadastroRestaurante.buscarOuFalhar(restauranteId);
	    assertTrue(restauranteDepois.getFormasPagamento()
	            .stream()
	            .anyMatch(fp -> fp.getId().equals(formaPagamentoId)));
	}

	@Test
	public void deveDesassociarFormaPagamento() {
	    Long restauranteId = 1L;
	    Long formaPagamentoId = 2L;

	    // Antes: restaurante possui forma de pagamento
	    Restaurante restauranteAntes = cadastroRestaurante.buscarOuFalhar(restauranteId);
	    assertTrue(restauranteAntes.getFormasPagamento()
	            .stream()
	            .anyMatch(fp -> fp.getId().equals(formaPagamentoId)));

	    // Executa a associação
	    cadastroRestaurante.desassociarFormaPagamento(restauranteId, formaPagamentoId);

	    // Depois: restaurante não deve possuir a forma de pagamento
	    Restaurante restauranteDepois = cadastroRestaurante.buscarOuFalhar(restauranteId);
	    assertFalse(restauranteDepois.getFormasPagamento()
	            .stream()
	            .anyMatch(fp -> fp.getId().equals(formaPagamentoId)));
	}	
}

package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.CozinhaNaoEncontradaException;
import com.elienai.springfood.domain.exception.EntidadeEmUsoException;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.repository.CozinhaRepository;
import com.elienai.springfood.util.DatabaseCleaner;

/**
 * Testes de integração para CadastroCozinhaService.
 * 
 * Cobrem:
 *  - Busca por ID
 *  - Inclusão
 *  - Exclusão
 *  - Exceções quando a cozinha não existe ou está em uso
 * 
 * Dados iniciais carregados via @Sql.
*/

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroCozinhaServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroCozinhaServiceIT {

	@Autowired
	private CadastroCozinhaService cadastroCozinha;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;

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
	public void deveRetornarCozinhaPorId() {
		Cozinha cozinha = cadastroCozinha.buscarOuFalhar(1L);
		
		assertNotNull(cozinha);
		assertEquals("Italiana", cozinha.getNome());
	}
	
	@Test
	public void deveSalvarCozinha() {
		Cozinha cozinha = new Cozinha();
		cozinha.setNome("Árabe");
		
		Cozinha cozinhaSalvo = cadastroCozinha.salvar(cozinha);

		// Valida o objeto salvo
		assertNotNull(cozinhaSalvo);
		assertNotNull(cozinhaSalvo.getId());
		assertEquals("Árabe", cozinhaSalvo.getNome());
		
		// Confirma persistência no banco via Repository
		Optional<Cozinha> optCozinha = cozinhaRepository.findById(cozinhaSalvo.getId());
		assertTrue(optCozinha.isPresent());
		assertEquals("Árabe", optCozinha.get().getNome());
	}	
	
	@Test
	public void deveExcluirCozinha() {
		cadastroCozinha.excluir(1L);

		// Confirma persistência no banco via Repository
		Optional<Cozinha> optCozinha = cozinhaRepository.findById(1L);
		assertTrue(optCozinha.isEmpty());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoCozinhaInexistente() {
		CozinhaNaoEncontradaException ex = assertThrows(CozinhaNaoEncontradaException.class, () -> cadastroCozinha.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de cozinha com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoCozinhaInexistente() {
		CozinhaNaoEncontradaException ex = assertThrows(CozinhaNaoEncontradaException.class, () -> cadastroCozinha.excluir(99L));
		
		assertEquals("Não existe um cadastro de cozinha com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoCozinhaoEmUso() {
		EntidadeEmUsoException ex = assertThrows(EntidadeEmUsoException.class, () -> cadastroCozinha.excluir(2L));
		
		assertEquals("Cozinha de código 2 não pode ser removida, pois está em uso", ex.getMessage());
	}		
}

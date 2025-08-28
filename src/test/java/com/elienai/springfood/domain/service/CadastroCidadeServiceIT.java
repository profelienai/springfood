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

import com.elienai.springfood.domain.exception.CidadeNaoEncontradaException;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.CidadeRepository;
import com.elienai.springfood.util.DatabaseCleaner;

/**
 * Testes de integração para CadastroCidadeService.
 * 
 * Cobrem:
 *  - Busca por ID
 *  - Inclusão
 *  - Exclusão
 *  - Exceções quando a cidade não existe
 * 
 * Dados iniciais carregados via @Sql.
*/

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroCidadeServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroCidadeServiceIT {

	@Autowired
	private CadastroCidadeService cadastroCidade;
	
	@Autowired
	private CidadeRepository cidadeRepository;

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
	public void deveRetornarCidadePorId() {
		Cidade cidade = cadastroCidade.buscarOuFalhar(1L);
		
		assertNotNull(cidade);
		assertEquals("Belo Horizonte", cidade.getNome());
	}
	
	@Test
	public void deveSalvarCidade() {
		Estado estado = new Estado();
		estado.setId(1L);

		Cidade cidade = new Cidade();
		cidade.setEstado(estado);
		cidade.setNome("Pindamonhangaba");
		
		Cidade cidadeSalvo = cadastroCidade.salvar(cidade);

		// Valida o objeto salvo
		assertNotNull(cidadeSalvo);
		assertNotNull(cidadeSalvo.getId());
		assertEquals("Pindamonhangaba", cidadeSalvo.getNome());
		
		// Confirma persistência no banco via Repository
		Optional<Cidade> optCidade = cidadeRepository.findById(cidadeSalvo.getId());
		assertTrue(optCidade.isPresent());
		assertEquals("Pindamonhangaba", optCidade.get().getNome());
	}	
	
	@Test
	public void deveExcluirCidade() {
		cadastroCidade.excluir(1L);

		// Confirma persistência no banco via Repository
		Optional<Cidade> optCidade = cidadeRepository.findById(1L);
		assertTrue(optCidade.isEmpty());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoCidadeInexistente() {
		CidadeNaoEncontradaException ex = assertThrows(CidadeNaoEncontradaException.class, () -> cadastroCidade.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de cidade com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoCidadeInexistente() {
		CidadeNaoEncontradaException ex = assertThrows(CidadeNaoEncontradaException.class, () -> cadastroCidade.excluir(99L));
		
		assertEquals("Não existe um cadastro de cidade com código 99", ex.getMessage());
	}	
}

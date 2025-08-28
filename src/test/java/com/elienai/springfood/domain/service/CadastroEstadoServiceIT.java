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

import com.elienai.springfood.domain.exception.EntidadeEmUsoException;
import com.elienai.springfood.domain.exception.EstadoNaoEncontradoException;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.EstadoRepository;
import com.elienai.springfood.util.DatabaseCleaner;

/**
 * Testes de integração para CadastroEstadoService.
 * 
 * Cobrem:
 *  - Busca por ID
 *  - Inclusão
 *  - Exclusão
 *  - Exceções quando o estado não existe ou está em uso
 * 
 * Dados iniciais carregados via @Sql.
*/

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroEstadoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroEstadoServiceIT {

	@Autowired
	private CadastroEstadoService cadastroEstado;
	
	@Autowired
	private EstadoRepository estadoRepository;

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
	public void deveRetornarEstadoPorId() {
		Estado estado = cadastroEstado.buscarOuFalhar(1L);
		
		assertNotNull(estado);
		assertEquals("Rio de Janeiro", estado.getNome());
	}
	
	@Test
	public void deveSalvarEstado() {
		Estado estado = new Estado();
		estado.setNome("São Paulo");
		
		Estado estadoSalvo = cadastroEstado.salvar(estado);

		// Valida o objeto salvo
		assertNotNull(estadoSalvo);
		assertNotNull(estadoSalvo.getId());
		assertEquals("São Paulo", estadoSalvo.getNome());
		
		// Confirma persistência no banco via Repository
		Optional<Estado> optEstado = estadoRepository.findById(estadoSalvo.getId());
		assertTrue(optEstado.isPresent());
		assertEquals("São Paulo", optEstado.get().getNome());
	}	
	
	@Test
	public void deveExcluirEstado() {
		cadastroEstado.excluir(1L);

		// Confirma persistência no banco via Repository
		Optional<Estado> optEstado = estadoRepository.findById(1L);
		assertTrue(optEstado.isEmpty());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoEstadoInexistente() {
		EstadoNaoEncontradoException ex = assertThrows(EstadoNaoEncontradoException.class, () -> cadastroEstado.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de estado com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoEstadoInexistente() {
		EstadoNaoEncontradoException ex = assertThrows(EstadoNaoEncontradoException.class, () -> cadastroEstado.excluir(99L));
		
		assertEquals("Não existe um cadastro de estado com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoEstadoEmUso() {
		EntidadeEmUsoException ex = assertThrows(EntidadeEmUsoException.class, () -> cadastroEstado.excluir(2L));
		
		assertEquals("Estado de código 2 não pode ser removido, pois está em uso", ex.getMessage());
	}		
}

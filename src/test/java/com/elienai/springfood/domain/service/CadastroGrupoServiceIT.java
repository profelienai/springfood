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
import com.elienai.springfood.domain.exception.GrupoNaoEncontradoException;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.repository.GrupoRepository;
import com.elienai.springfood.util.DatabaseCleaner;

/**
 * Testes de integração para CadastroGrupoService.
 * 
 * Cobrem:
 *  - Busca por ID
 *  - Inclusão
 *  - Exclusão
 *  - Exceções quando o grupo não existe ou está em uso
 * 
 * Dados iniciais carregados via @Sql.
*/

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroGrupoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroGrupoServiceIT {

	@Autowired
	private CadastroGrupoService cadastroGrupo;
	
	@Autowired
	private GrupoRepository grupoRepository;

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
	public void deveRetornarGrupoPorId() {
		Grupo grupo = cadastroGrupo.buscarOuFalhar(1L);
		
		assertNotNull(grupo);
		assertEquals("Gerente", grupo.getNome());
	}
	
	@Test
	public void deveSalvarGrupo() {
		Grupo grupo = new Grupo();
		grupo.setNome("Secretaria");
		
		Grupo grupoSalvo = cadastroGrupo.salvar(grupo);

		// Valida o objeto salvo
		assertNotNull(grupoSalvo);
		assertNotNull(grupoSalvo.getId());
		assertEquals("Secretaria", grupoSalvo.getNome());
		
		// Confirma persistência no banco via Repository
		Optional<Grupo> optGrupo = grupoRepository.findById(grupoSalvo.getId());
		assertTrue(optGrupo.isPresent());
		assertEquals("Secretaria", optGrupo.get().getNome());
	}	
	
	@Test
	public void deveExcluirGrupo() {
		cadastroGrupo.excluir(2L);

		// Confirma persistência no banco via Repository
		Optional<Grupo> optGrupo = grupoRepository.findById(2L);
		assertTrue(optGrupo.isEmpty());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoGrupoInexistente() {
		GrupoNaoEncontradoException ex = assertThrows(GrupoNaoEncontradoException.class, () -> cadastroGrupo.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de grupo com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoGrupoInexistente() {
		GrupoNaoEncontradoException ex = assertThrows(GrupoNaoEncontradoException.class, () -> cadastroGrupo.excluir(99L));
		
		assertEquals("Não existe um cadastro de grupo com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoGrupoEmUso() {
		EntidadeEmUsoException ex = assertThrows(EntidadeEmUsoException.class, () -> cadastroGrupo.excluir(1L));
		
		assertEquals("Grupo de código 1 não pode ser removido, pois está em uso", ex.getMessage());
	}		
}

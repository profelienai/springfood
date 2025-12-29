package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.PermissaoNaoEncontradaException;
import com.elienai.springfood.domain.model.Permissao;
import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroPermissaoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroPermissaoServiceIT {

	@Autowired
	private CadastroPermissaoService cadastroPermissao;
	
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
	public void deveRetornarPermissaoPorId() {
		Permissao permissao = cadastroPermissao.buscarOuFalhar(1L);
		
		assertNotNull(permissao);
		assertEquals("CONSULTAR_COZINHAS", permissao.getNome());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoPermissaoInexistente() {
		PermissaoNaoEncontradaException ex = assertThrows(PermissaoNaoEncontradaException.class, () -> cadastroPermissao.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de permissão com código 99", ex.getMessage());
	}	
}

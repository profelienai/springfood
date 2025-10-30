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
import com.elienai.springfood.domain.exception.FormaPagamentoNaoEncontradaException;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.repository.FormaPagamentoRepository;
import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroFormaPagamentoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroFormaPagamentoServiceIT {

	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamento;
	
	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;

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
	public void deveRetornarFormaPagamentoPorId() {
		var formaPagamento = cadastroFormaPagamento.buscarOuFalhar(1L);
		
		assertNotNull(formaPagamento);
		assertEquals("Cartao de credito", formaPagamento.getDescricao());
	}
	
	@Test
	public void deveSalvarFormaPagamento() {
		var formaPagamento = new FormaPagamento();
		formaPagamento.setDescricao("PIX");
		
		var formaPagamentoSalvo = cadastroFormaPagamento.salvar(formaPagamento);

		// Valida o objeto salvo
		assertNotNull(formaPagamentoSalvo);
		assertNotNull(formaPagamentoSalvo.getId());
		assertEquals("PIX", formaPagamentoSalvo.getDescricao());
		
		// Confirma persistência no banco via Repository
		Optional<FormaPagamento> optFormaPagamento = formaPagamentoRepository.findById(formaPagamentoSalvo.getId());
		assertTrue(optFormaPagamento.isPresent());
		assertEquals("PIX", optFormaPagamento.get().getDescricao());
	}	
	
	@Test
	public void deveExcluirFormaPagamento() {
		cadastroFormaPagamento.excluir(3L);

		// Confirma persistência no banco via Repository
		Optional<FormaPagamento> optFormaPagamento = formaPagamentoRepository.findById(3L);
		assertTrue(optFormaPagamento.isEmpty());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoFormaPagamentoInexistente() {
		FormaPagamentoNaoEncontradaException ex = assertThrows(FormaPagamentoNaoEncontradaException.class, () -> cadastroFormaPagamento.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de forma de pagamento com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoFormaPagamentoInexistente() {
		FormaPagamentoNaoEncontradaException ex = assertThrows(FormaPagamentoNaoEncontradaException.class, () -> cadastroFormaPagamento.excluir(99L));
		
		assertEquals("Não existe um cadastro de forma de pagamento com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveLancarExcecaoAoExcluir_QuandoFormaPagamentoEmUso() {
		EntidadeEmUsoException ex = assertThrows(EntidadeEmUsoException.class, () -> cadastroFormaPagamento.excluir(2L));
		
		assertEquals("Forma de pagamento de código 2 não pode ser removida, pois está em uso", ex.getMessage());
	}		
}

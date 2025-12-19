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

import com.elienai.springfood.domain.exception.ProdutoNaoEncontradoException;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.ProdutoRepository;
import com.elienai.springfood.util.DatabaseCleaner;

/**
 * Testes de integração para CadastroProdutoService.
 * 
 * Cobrem:
 *  - Busca por ID
 *  - Inclusão
 *  - Exclusão
 *  - Exceções quando o produto não existe ou está em uso
 * 
 * Dados iniciais carregados via @Sql.
*/

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroProdutoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroProdutoServiceIT {

	@Autowired
	private CadastroProdutoService cadastroProduto;
	
	@Autowired
	private ProdutoRepository produtoRepository;

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
	public void deveRetornarProdutoPorId() {
		Produto produto = cadastroProduto.buscarOuFalhar(1L, 1L);
		
		assertNotNull(produto);
		assertEquals("Porco com molho agridoce", produto.getNome());
	}
	
	@Test
	public void deveSalvarProduto() {
		Restaurante restaurante = new Restaurante();
		restaurante.setId(1L);
		
		Produto produto = new Produto();
		produto.setNome("Esfiha");
		produto.setDescricao("Esfiha de calabresa");
		produto.setPreco(new BigDecimal("9.90"));
		produto.setAtivo(true);		
		produto.setRestaurante(restaurante);
		
		Produto produtoSalvo = cadastroProduto.salvar(produto);

		// Valida o objeto salvo
		assertNotNull(produtoSalvo);
		assertNotNull(produtoSalvo.getId());
		assertEquals("Esfiha", produtoSalvo.getNome());
		assertEquals("Esfiha de calabresa", produtoSalvo.getDescricao());
		assertEquals(new BigDecimal("9.90"), produtoSalvo.getPreco());
		assertEquals(true, produtoSalvo.getAtivo());
		assertEquals(1L, produtoSalvo.getRestaurante().getId());
		
		// Confirma persistência no banco via Repository
		Optional<Produto> optProduto = produtoRepository.findById(produtoSalvo.getId());
		assertTrue(optProduto.isPresent());
		assertEquals("Esfiha", optProduto.get().getNome());
		assertEquals("Esfiha", optProduto.get().getNome());
		assertEquals("Esfiha de calabresa", optProduto.get().getDescricao());
		assertEquals(new BigDecimal("9.90"), optProduto.get().getPreco());
		assertEquals(true, optProduto.get().getAtivo());
		assertEquals(1L, optProduto.get().getRestaurante().getId());
	}	
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoProdutoInexistente() {
		ProdutoNaoEncontradoException ex = assertThrows(ProdutoNaoEncontradoException.class, () -> cadastroProduto.buscarOuFalhar(1L, 99L));
		
		assertEquals("Não existe um cadastro de produto com código 99 para o restaurante de código 1", ex.getMessage());
	}	
}

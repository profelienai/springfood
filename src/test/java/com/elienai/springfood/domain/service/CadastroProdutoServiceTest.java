package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroProdutoServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;
	
	@InjectMocks
	private CadastroProdutoService cadastroProdutoService;
	
	private Produto produto;
	
	@BeforeEach
	void setUp() {
		produto = new Produto();
		produto.setId(1L);
	}
	
	@Test
	void testSalvar_ComSucesso() {
		when(produtoRepository.save(produto)).thenReturn(produto);
		
		Produto produtoSalvo = cadastroProdutoService.salvar(produto);
		
		assertNotNull(produtoSalvo);
		assertSame(produto, produtoSalvo);
		
		verify(produtoRepository).save(produto);
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long produtoId = 10L;
		Long restauranteId = 2L;
		
		when(produtoRepository.findById(restauranteId, produtoId)).thenReturn(Optional.of(produto));
		
		Produto produtoEncontrado = cadastroProdutoService.buscarOuFalhar(restauranteId, produtoId);
	
		assertNotNull(produtoEncontrado);
		assertSame(produto, produtoEncontrado);
		
		verify(produtoRepository).findById(restauranteId, produtoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoProdutoNaoExiste() {
		Long produtoId = 999L;
		Long restauranteId = 2L;
		
		when(produtoRepository.findById(restauranteId, produtoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroProdutoService.buscarOuFalhar(restauranteId, produtoId));
		
		assertEquals("Não existe um cadastro de produto com código 999 para o restaurante de código 2", ex.getMessage());
		verify(produtoRepository).findById(restauranteId, produtoId);
	}	
}

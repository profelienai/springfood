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
import com.elienai.springfood.domain.model.Permissao;
import com.elienai.springfood.domain.repository.PermissaoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroPermissaoServiceTest {

	@Mock
	private PermissaoRepository permissaoRepository;
	
	@InjectMocks
	private CadastroPermissaoService cadastroPermissaoService;
	
	private Permissao permissao;
	
	@BeforeEach
	void setUp() {
		permissao = new Permissao();
		permissao.setId(1L);
	}	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long permissaoId = 10L;
		
		when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.of(permissao));
		
		Permissao permissaoEncontrado = cadastroPermissaoService.buscarOuFalhar(permissaoId);
	
		assertNotNull(permissaoEncontrado);
		assertSame(permissao, permissaoEncontrado);
		
		verify(permissaoRepository).findById(permissaoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoPermissaoNaoExiste() {
		Long permissaoId = 999L;
		
		when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroPermissaoService.buscarOuFalhar(permissaoId));
		
		assertEquals("Não existe um cadastro de permissão com código 999", ex.getMessage());
		verify(permissaoRepository).findById(permissaoId);
	}	
}

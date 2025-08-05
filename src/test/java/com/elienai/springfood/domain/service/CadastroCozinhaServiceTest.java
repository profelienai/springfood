package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.elienai.springfood.domain.exception.EntidadeEmUsoException;
import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.repository.CozinhaRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroCozinhaServiceTest {

	@Mock
	private CozinhaRepository cozinhaRepository;
	
	@InjectMocks
	private CadastroCozinhaService cadastroCozinhaService;
	
	private Cozinha cozinha;
	
	@BeforeEach
	void setUp() {
		cozinha = new Cozinha();
		cozinha.setId(1L);
	}
	
	@Test
	void testSalvar_ComSucesso() {
		when(cozinhaRepository.save(cozinha)).thenReturn(cozinha);
		
		Cozinha cozinhaSalva = cadastroCozinhaService.salvar(cozinha);
		
		assertNotNull(cozinhaSalva);
		assertSame(cozinha, cozinhaSalva);
		
		verify(cozinhaRepository).save(cozinha);
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long cozinhaId = 10L;
		
		doNothing().when(cozinhaRepository).deleteById(cozinhaId);
		
		assertDoesNotThrow(() -> cadastroCozinhaService.excluir(cozinhaId));
		verify(cozinhaRepository).deleteById(cozinhaId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoCozinhaNaoExiste() {
		Long cozinhaId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(cozinhaRepository).deleteById(cozinhaId);
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroCozinhaService.excluir(cozinhaId));
		
		assertEquals("Não existe um cadastro de cozinha com código 999", ex.getMessage());
		verify(cozinhaRepository).deleteById(cozinhaId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoCozinhaEstaEmUso() {
		Long cozinhaId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(cozinhaRepository).deleteById(cozinhaId);
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroCozinhaService.excluir(cozinhaId));
		
		assertEquals("Cozinha de código 10 não pode ser removida, pois está em uso", ex.getMessage());
		verify(cozinhaRepository).deleteById(cozinhaId);		
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long cozinhaId = 10L;
		
		when(cozinhaRepository.findById(cozinhaId)).thenReturn(Optional.of(cozinha));
		
		Cozinha cozinhaEncontrada = cadastroCozinhaService.buscarOuFalhar(cozinhaId);
	
		assertNotNull(cozinhaEncontrada);
		assertSame(cozinha, cozinhaEncontrada);
		
		verify(cozinhaRepository).findById(cozinhaId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoCozinhaNaoExiste() {
		Long cozinhaId = 999L;
		
		when(cozinhaRepository.findById(cozinhaId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroCozinhaService.buscarOuFalhar(cozinhaId));
		
		assertEquals("Não existe um cadastro de cozinha com código 999", ex.getMessage());
		verify(cozinhaRepository).findById(cozinhaId);
	}	
}

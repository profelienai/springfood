package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.elienai.springfood.domain.exception.CozinhaNaoEncontradaException;
import com.elienai.springfood.domain.exception.EntidadeEmUsoException;
import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.RestauranteRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroRestauranteServiceTest {

	@Mock
	private RestauranteRepository restauranteRepository;
	
	@InjectMocks
	private CadastroRestauranteService cadastroRestauranteService;
	
	@Mock
	private CadastroCozinhaService cadastroCozinha;
	
	private Cozinha cozinha;
	private Restaurante restaurante10;
	private Restaurante restaurante20;
	
	@BeforeEach
	void setUp() {
		cozinha = new Cozinha();
		cozinha.setId(1L);
		
		restaurante10 = new Restaurante();
		restaurante10.setId(10L);
		restaurante10.setCozinha(cozinha);
		restaurante10.setNome("NomeTeste");
		
		restaurante20 = new Restaurante();
		restaurante20.setId(20L);
	}
	
	@Test
	void testSalvar_RestauranteComCozinhaExistente() {
		when(cadastroCozinha.buscarOuFalhar(1L)).thenReturn(cozinha);
		when(restauranteRepository.save(restaurante10)).thenReturn(restaurante10);
		
		Restaurante restauranteSalvo = cadastroRestauranteService.salvar(restaurante10);
		
		assertNotNull(restauranteSalvo);
		assertSame(restaurante10, restauranteSalvo);
		
		verify(cadastroCozinha).buscarOuFalhar(1L);
		verify(restauranteRepository).save(restaurante10);
	}
	
	@Test
	void testSalvar_LancarExcecaoQuandoCozinhaNaoExiste() {
		when(cadastroCozinha.buscarOuFalhar(1L)).thenThrow(new CozinhaNaoEncontradaException(1L));
		
		EntidadeNaoEncontradaException ex = 
				assertThrows(EntidadeNaoEncontradaException.class,() -> cadastroRestauranteService.salvar(restaurante10));
		
		assertEquals("Não existe um cadastro de cozinha com código 1", ex.getMessage());
		verify(cadastroCozinha).buscarOuFalhar(1L);
		verify(restauranteRepository, never()).save(any());
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long restauranteId = 10L;
		
		doNothing().when(restauranteRepository).deleteById(restauranteId);
		
		assertDoesNotThrow(() -> cadastroRestauranteService.excluir(restauranteId));
		verify(restauranteRepository).deleteById(restauranteId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoRestauranteNaoExiste() {
		Long restauranteId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(restauranteRepository).deleteById(restauranteId);
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroRestauranteService.excluir(restauranteId));
		
		assertEquals("Não existe um cadastro de restaurante com código 999", ex.getMessage());
		verify(restauranteRepository).deleteById(restauranteId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoRestauranteEstaEmUso() {
		Long restauranteId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(restauranteRepository).deleteById(restauranteId);
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroRestauranteService.excluir(restauranteId));
		
		assertEquals("Restaurante de código 10 não pode ser removido, pois está em uso", ex.getMessage());
		verify(restauranteRepository).deleteById(restauranteId);		
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long restauranteId = 10L;
		
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante10));
		
		Restaurante restauranteEncontrado = cadastroRestauranteService.buscarOuFalhar(restauranteId);
	
		assertNotNull(restauranteEncontrado);
		assertSame(restaurante10, restauranteEncontrado);
		
		verify(restauranteRepository).findById(restauranteId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoRestauranteNaoExiste() {
		Long restauranteId = 999L;
		
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroRestauranteService.buscarOuFalhar(restauranteId));
		
		assertEquals("Não existe um cadastro de restaurante com código 999", ex.getMessage());
		verify(restauranteRepository).findById(restauranteId);
	}
	
	@Test
	void testAtivar_ComSucesso() {
		Long restauranteId = 10L;
		restaurante10.setAtivo(false);
		
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante10));

		assertDoesNotThrow(() -> cadastroRestauranteService.ativar(restauranteId));

		assertTrue(restaurante10.getAtivo());
		verify(restauranteRepository).findById(restauranteId);
	}	
	
	@Test
	void testInativar_ComSucesso() {
		Long restauranteId = 10L;
		restaurante10.setAtivo(true);
		
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante10));

		assertDoesNotThrow(() -> cadastroRestauranteService.inativar(restauranteId));

		assertFalse(restaurante10.getAtivo());
		verify(restauranteRepository).findById(restauranteId);
	}
	
	@Test
	void testAtivar_LancarExcecaoQuandoRestauranteNaoExiste() {
		Long restauranteId = 999L;
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.empty());

		EntidadeNaoEncontradaException ex = 
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroRestauranteService.ativar(restauranteId));

		assertEquals("Não existe um cadastro de restaurante com código 999", ex.getMessage());
		verify(restauranteRepository).findById(restauranteId);
	}
	
	@Test
	void testInativar_LancarExcecaoQuandoRestauranteNaoExiste() {
		Long restauranteId = 999L;
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.empty());

		EntidadeNaoEncontradaException ex = 
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroRestauranteService.inativar(restauranteId));

		assertEquals("Não existe um cadastro de restaurante com código 999", ex.getMessage());
		verify(restauranteRepository).findById(restauranteId);
	}

	@Test
	void testAtivarLista_ComSucesso() {
		restaurante10.setAtivo(false);
		restaurante10.setAtivo(false);
		
		when(restauranteRepository.findById(10L)).thenReturn(Optional.of(restaurante10));
		when(restauranteRepository.findById(20L)).thenReturn(Optional.of(restaurante20));

		assertDoesNotThrow(() -> cadastroRestauranteService.ativar(Arrays.asList(10L, 20L)));

		assertTrue(restaurante10.getAtivo());
		assertTrue(restaurante20.getAtivo());		
		
		verify(restauranteRepository).findById(10L);
		verify(restauranteRepository).findById(20L);
	}

	@Test
	void testInativarLista_ComSucesso() {
		restaurante10.setAtivo(true);
		restaurante10.setAtivo(true);
		
		when(restauranteRepository.findById(10L)).thenReturn(Optional.of(restaurante10));
		when(restauranteRepository.findById(20L)).thenReturn(Optional.of(restaurante20));

		assertDoesNotThrow(() -> cadastroRestauranteService.inativar(Arrays.asList(10L, 20L)));

		assertFalse(restaurante10.getAtivo());
		assertFalse(restaurante20.getAtivo());
		
		verify(restauranteRepository).findById(10L);
		verify(restauranteRepository).findById(20L);
	}	
}

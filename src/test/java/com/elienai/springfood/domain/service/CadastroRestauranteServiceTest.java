package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
	private Restaurante restaurante;
	
	@BeforeEach
	void setUp() {
		cozinha = new Cozinha();
		cozinha.setId(1L);
		
		restaurante = new Restaurante();
		restaurante.setId(10L);
		restaurante.setCozinha(cozinha);
		restaurante.setNome("NomeTeste");
	}
	
	@Test
	void testSalvar_RestauranteComCozinhaExistente() {
		when(cadastroCozinha.buscarOuFalhar(1L)).thenReturn(cozinha);
		when(restauranteRepository.save(restaurante)).thenReturn(restaurante);
		
		Restaurante restauranteSalvo = cadastroRestauranteService.salvar(restaurante);
		
		assertNotNull(restauranteSalvo);
		assertSame(restaurante, restauranteSalvo);
		
		verify(cadastroCozinha).buscarOuFalhar(1L);
		verify(restauranteRepository).save(restaurante);
	}
	
	@Test
	void testSalvar_LancarExcecaoQuandoCozinhaNaoExiste() {
		when(cadastroCozinha.buscarOuFalhar(1L)).thenThrow(new CozinhaNaoEncontradaException(1L));
		
		EntidadeNaoEncontradaException ex = 
				assertThrows(EntidadeNaoEncontradaException.class,() -> cadastroRestauranteService.salvar(restaurante));
		
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
		
		when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));
		
		Restaurante restauranteEncontrado = cadastroRestauranteService.buscarOuFalhar(restauranteId);
	
		assertNotNull(restauranteEncontrado);
		assertSame(restaurante, restauranteEncontrado);
		
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
}

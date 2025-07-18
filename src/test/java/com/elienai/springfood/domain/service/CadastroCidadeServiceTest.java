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
import static org.mockito.Mockito.times;
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
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.CidadeRepository;
import com.elienai.springfood.domain.repository.EstadoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroCidadeServiceTest {

	@Mock
	private CidadeRepository cidadeRepository;
	
	@Mock
	private EstadoRepository estadoRepository;
	
	@InjectMocks
	private CadastroCidadeService cadastroCidadeService;
	
	private Estado estado;
	private Cidade cidade;
	
	@BeforeEach
	void setUp() {
		estado = new Estado();
		estado.setId(1L);
		
		cidade = new Cidade();
		cidade.setId(10L);
		cidade.setNome("Salvador");
		cidade.setEstado(estado);
	}
	
	@Test
	void testSalvar_CidadeComEstadoExistente() {
		when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
		when(cidadeRepository.save(cidade)).thenReturn(cidade);
		
		Cidade cidadeSalva = cadastroCidadeService.salvar(cidade);
		
		assertNotNull(cidadeSalva);
		assertSame(cidade, cidadeSalva);
		
		verify(estadoRepository, times(1)).findById(1L);
		verify(cidadeRepository, times(1)).save(cidade);
	}
	
	@Test
	void testSalvar_LancarExcecaoQuandoEstadoNaoExiste() {
		when(estadoRepository.findById(1L)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex = 
				assertThrows(EntidadeNaoEncontradaException.class,() -> cadastroCidadeService.salvar(cidade));
		
		assertEquals("Não existe cadastro de estado com código 1", ex.getMessage());
		verify(estadoRepository).findById(1L);
		verify(cidadeRepository, never()).save(any());
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long cidadeId = 10L;
		
		doNothing().when(cidadeRepository).deleteById(cidadeId);
		
		assertDoesNotThrow(() -> cadastroCidadeService.excluir(cidadeId));
		verify(cidadeRepository).deleteById(cidadeId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoCidadeNaoExiste() {
		Long cidadeId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(cidadeRepository).deleteById(cidadeId);
		
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroCidadeService.excluir(cidadeId));
		
		assertEquals("Não existe um cadastro de cidade com código 999", ex.getMessage());
		verify(cidadeRepository).deleteById(cidadeId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoCidadeEstaEmUso() {
		Long cidadeId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(cidadeRepository).deleteById(cidadeId);
		
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroCidadeService.excluir(cidadeId));
		
		assertEquals("Cidade de código 10 não pode ser removida, pois está em uso", ex.getMessage());
		verify(cidadeRepository).deleteById(cidadeId);		
	}
}

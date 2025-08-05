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
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.EstadoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroEstadoServiceTest {

	@Mock
	private EstadoRepository estadoRepository;
	
	@InjectMocks
	private CadastroEstadoService cadastroEstadoService;
	
	private Estado estado;
	
	@BeforeEach
	void setUp() {
		estado = new Estado();
		estado.setId(1L);
	}
	
	@Test
	void testSalvar_ComSucesso() {
		when(estadoRepository.save(estado)).thenReturn(estado);
		
		Estado estadoSalvo = cadastroEstadoService.salvar(estado);
		
		assertNotNull(estadoSalvo);
		assertSame(estado, estadoSalvo);
		
		verify(estadoRepository).save(estado);
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long estadoId = 10L;
		
		doNothing().when(estadoRepository).deleteById(estadoId);
		
		assertDoesNotThrow(() -> cadastroEstadoService.excluir(estadoId));
		verify(estadoRepository).deleteById(estadoId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoEstadoNaoExiste() {
		Long estadoId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(estadoRepository).deleteById(estadoId);
		
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroEstadoService.excluir(estadoId));
		
		assertEquals("Não existe um cadastro de estado com código 999", ex.getMessage());
		verify(estadoRepository).deleteById(estadoId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoEstadoEstaEmUso() {
		Long estadoId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(estadoRepository).deleteById(estadoId);
		
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroEstadoService.excluir(estadoId));
		
		assertEquals("Estado de código 10 não pode ser removido, pois está em uso", ex.getMessage());
		verify(estadoRepository).deleteById(estadoId);		
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long estadoId = 10L;
		
		when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estado));
		
		Estado estadoEncontrado = cadastroEstadoService.buscarOuFalhar(estadoId);
	
		assertNotNull(estadoEncontrado);
		assertSame(estado, estadoEncontrado);
		
		verify(estadoRepository).findById(estadoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoEstadoNaoExiste() {
		Long estadoId = 999L;
		
		when(estadoRepository.findById(estadoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroEstadoService.buscarOuFalhar(estadoId));
		
		assertEquals("Não existe um cadastro de estado com código 999", ex.getMessage());
		verify(estadoRepository).findById(estadoId);
	}	
}

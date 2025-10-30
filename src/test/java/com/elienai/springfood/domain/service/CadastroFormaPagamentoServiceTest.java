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
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.repository.FormaPagamentoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroFormaPagamentoServiceTest {

	@Mock
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@InjectMocks
	private CadastroFormaPagamentoService cadastroFormaPagamentoService;
	
	private FormaPagamento formaPagamento;
	
	@BeforeEach
	void setUp() {
		formaPagamento = new FormaPagamento();
		formaPagamento.setId(1L);
	}
	
	@Test
	void testSalvar_ComSucesso() {
		when(formaPagamentoRepository.save(formaPagamento)).thenReturn(formaPagamento);
		
		FormaPagamento formaPagamentoSalvo = cadastroFormaPagamentoService.salvar(formaPagamento);
		
		assertNotNull(formaPagamentoSalvo);
		assertSame(formaPagamento, formaPagamentoSalvo);
		
		verify(formaPagamentoRepository).save(formaPagamento);
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long formaPagamentoId = 10L;
		
		doNothing().when(formaPagamentoRepository).deleteById(formaPagamentoId);
		
		assertDoesNotThrow(() -> cadastroFormaPagamentoService.excluir(formaPagamentoId));
		verify(formaPagamentoRepository).deleteById(formaPagamentoId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoFormaPagamentoNaoExiste() {
		Long formaPagamentoId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(formaPagamentoRepository).deleteById(formaPagamentoId);
		
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroFormaPagamentoService.excluir(formaPagamentoId));
		
		assertEquals("Não existe um cadastro de forma de pagamento com código 999", ex.getMessage());
		verify(formaPagamentoRepository).deleteById(formaPagamentoId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoFormaPagamentoEstaEmUso() {
		Long formaPagamentoId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(formaPagamentoRepository).deleteById(formaPagamentoId);
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroFormaPagamentoService.excluir(formaPagamentoId));
		
		assertEquals("Forma de pagamento de código 10 não pode ser removida, pois está em uso", ex.getMessage());
		verify(formaPagamentoRepository).deleteById(formaPagamentoId);		
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long formaPagamentoId = 10L;
		
		when(formaPagamentoRepository.findById(formaPagamentoId)).thenReturn(Optional.of(formaPagamento));
		
		FormaPagamento formaPagamentoEncontrada = cadastroFormaPagamentoService.buscarOuFalhar(formaPagamentoId);
	
		assertNotNull(formaPagamentoEncontrada);
		assertSame(formaPagamento, formaPagamentoEncontrada);
		
		verify(formaPagamentoRepository).findById(formaPagamentoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoFormaPagamentoNaoExiste() {
		Long formaPagamentoId = 999L;
		
		when(formaPagamentoRepository.findById(formaPagamentoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroFormaPagamentoService.buscarOuFalhar(formaPagamentoId));
		
		assertEquals("Não existe um cadastro de forma de pagamento com código 999", ex.getMessage());
		verify(formaPagamentoRepository).findById(formaPagamentoId);
	}	
}

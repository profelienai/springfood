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
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
public class EmissaoPedidoServiceTest {

	@Mock
	private PedidoRepository pedidoRepository;
	
	@InjectMocks
	private EmissaoPedidoService emissaoPedidoService;
	
	private Pedido pedido;
	
	@BeforeEach
	void setUp() {
		pedido = new Pedido();
		pedido.setId(1L);
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long pedidoId = 10L;
		
		when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
		
		Pedido pedidoEncontrado = emissaoPedidoService.buscarOuFalhar(pedidoId);
	
		assertNotNull(pedidoEncontrado);
		assertSame(pedido, pedidoEncontrado);
		
		verify(pedidoRepository).findById(pedidoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoPedidoNaoExiste() {
		Long pedidoId = 999L;
		
		when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> emissaoPedidoService.buscarOuFalhar(pedidoId));
		
		assertEquals("Não existe um pedido com código 999", ex.getMessage());
		verify(pedidoRepository).findById(pedidoId);
	}	
}

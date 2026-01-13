package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.exception.ProdutoNaoEncontradoException;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.model.ItemPedido;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
public class EmissaoPedidoServiceTest {

	@Mock
	private PedidoRepository pedidoRepository;
	
	@InjectMocks
	private EmissaoPedidoService emissaoPedidoService;
	
	@Mock
	private CadastroRestauranteService cadastroRestaurante;

	@Mock
	private CadastroCidadeService cadastroCidade;

	@Mock
	private CadastroUsuarioService cadastroUsuario;

	@Mock
	private CadastroProdutoService cadastroProduto;

	@Mock
	private CadastroFormaPagamentoService cadastroFormaPagamento;	
	
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
	
	@Test
	void emitir_DeveEmitirPedidoComSucesso() {
	    Cidade cidade = new Cidade();
	    cidade.setId(1L);

	    Usuario cliente = new Usuario();
	    cliente.setId(2L);

	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(3L);
	    formaPagamento.setDescricao("Cartão");

	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(4L);
	    restaurante.setTaxaFrete(new BigDecimal("10.00"));
	    restaurante.adicionarFormaPagamento(formaPagamento);

	    Produto produto = new Produto();
	    produto.setId(5L);
	    produto.setPreco(new BigDecimal("20.00"));

	    Endereco endereco = new Endereco();
	    endereco.setCidade(cidade);

	    ItemPedido item = new ItemPedido();
	    item.setProduto(produto);
	    item.setQuantidade(2);

	    pedido.setEnderecoEntrega(endereco);
	    pedido.setCliente(cliente);
	    pedido.setRestaurante(restaurante);
	    pedido.setFormaPagamento(formaPagamento);
	    pedido.getItens().add(item);

	    when(cadastroCidade.buscarOuFalhar(1L)).thenReturn(cidade);
	    when(cadastroUsuario.buscarOuFalhar(2L)).thenReturn(cliente);
	    when(cadastroRestaurante.buscarOuFalhar(4L)).thenReturn(restaurante);
	    when(cadastroFormaPagamento.buscarOuFalhar(3L)).thenReturn(formaPagamento);
	    when(cadastroProduto.buscarOuFalhar(4L, 5L)).thenReturn(produto);
	    when(pedidoRepository.save(pedido)).thenReturn(pedido);

	    // act
	    Pedido pedidoEmitido = emissaoPedidoService.emitir(pedido);

	    // asserts
	    assertNotNull(pedidoEmitido);
	    assertEquals(new BigDecimal("40.00"), pedidoEmitido.getSubtotal());
	    assertEquals(new BigDecimal("10.00"), pedidoEmitido.getTaxaFrete());
	    assertEquals(new BigDecimal("50.00"), pedidoEmitido.getValorTotal());

	    ItemPedido itemEmitido = pedidoEmitido.getItens().get(0);
	    assertSame(pedidoEmitido, itemEmitido.getPedido());
	    assertEquals(new BigDecimal("20.00"), itemEmitido.getPrecoUnitario());

	    verify(pedidoRepository).save(pedido);
	}

	@Test
	void emitir_DeveLancarExcecaoQuandoFormaPagamentoNaoAceita() {
	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(1L);
	    formaPagamento.setDescricao("Pix");

	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(2L); // NÃO adiciona forma de pagamento

	    pedido.setFormaPagamento(formaPagamento);
	    pedido.setRestaurante(restaurante);
	    pedido.setEnderecoEntrega(new Endereco());
	    pedido.getEnderecoEntrega().setCidade(new Cidade());
	    pedido.getEnderecoEntrega().getCidade().setId(10L);
	    pedido.setCliente(new Usuario());
	    pedido.getCliente().setId(20L);

	    when(cadastroCidade.buscarOuFalhar(anyLong())).thenReturn(new Cidade());
	    when(cadastroUsuario.buscarOuFalhar(anyLong())).thenReturn(new Usuario());
	    when(cadastroRestaurante.buscarOuFalhar(anyLong())).thenReturn(restaurante);
	    when(cadastroFormaPagamento.buscarOuFalhar(anyLong())).thenReturn(formaPagamento);

	    // act
	    NegocioException ex = assertThrows(
	        NegocioException.class,
	        () -> emissaoPedidoService.emitir(pedido)
	    );

	    assertEquals("Forma de pagamento 'Pix' não é aceita por esse restaurante.", ex.getMessage());
	    verify(pedidoRepository, never()).save(any());
	}

	@Test
	void emitir_DevePropagarExcecaoQuandoProdutoNaoExiste() {
	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(3L);
	    formaPagamento.setDescricao("Cartão");

	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(4L);
	    restaurante.setTaxaFrete(new BigDecimal("10.00"));
	    restaurante.adicionarFormaPagamento(formaPagamento);
	    
	    Produto produto = new Produto();
	    produto.setId(99L);

	    ItemPedido item = new ItemPedido();
	    item.setProduto(produto);
	    item.setQuantidade(1);

	    pedido.getItens().add(item);
	    pedido.setRestaurante(restaurante);
	    pedido.getRestaurante().setId(1L);
	    pedido.setEnderecoEntrega(new Endereco());
	    pedido.getEnderecoEntrega().setCidade(new Cidade());
	    pedido.getEnderecoEntrega().getCidade().setId(1L);
	    pedido.setCliente(new Usuario());
	    pedido.getCliente().setId(2L);
	    pedido.setFormaPagamento(new FormaPagamento());
	    pedido.getFormaPagamento().setId(3L);

	    when(cadastroCidade.buscarOuFalhar(anyLong())).thenReturn(new Cidade());
	    when(cadastroUsuario.buscarOuFalhar(anyLong())).thenReturn(new Usuario());
	    when(cadastroRestaurante.buscarOuFalhar(anyLong())).thenReturn(restaurante);
	    when(cadastroFormaPagamento.buscarOuFalhar(anyLong())).thenReturn(formaPagamento);
	    when(cadastroProduto.buscarOuFalhar(anyLong(), anyLong()))
	        .thenThrow(new ProdutoNaoEncontradoException("Produto não encontrado"));

	    // act
	    var ex = assertThrows(
	        EntidadeNaoEncontradaException.class,
	        () -> emissaoPedidoService.emitir(pedido)
	    );

	    assertEquals("Produto não encontrado", ex.getMessage());

	    verify(pedidoRepository, never()).save(any());
	}
	
	
}

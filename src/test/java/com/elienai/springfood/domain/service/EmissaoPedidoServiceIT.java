package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.exception.PedidoNaoEncontradoException;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.model.ItemPedido;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/EmissaoPedidoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmissaoPedidoServiceIT {

	@Autowired
	private EmissaoPedidoService emissaoPedido;
	
    @Autowired
    private DatabaseCleaner databaseCleaner;

    /**
     * Limpa o banco após cada teste, garantindo isolamento entre os testes.
     */
    @AfterEach
    private void cleanDatabase() {
    	databaseCleaner.clearTables();
    }
    
	@Test
	public void deveRetornarPedidoPorId() {
		Pedido pedido = emissaoPedido.buscarOuFalhar(1L);
		
		assertNotNull(pedido);
		assertEquals(1L, pedido.getId());
	}
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoPedidoInexistente() {
		PedidoNaoEncontradoException ex = assertThrows(PedidoNaoEncontradoException.class, () -> emissaoPedido.buscarOuFalhar(99L));
		
		assertEquals("Não existe um pedido com código 99", ex.getMessage());
	}

	@Test
	public void deveEmitirPedidoComSucesso() {
	    Pedido pedido = new Pedido();

	    // Restaurante 1 aceita forma pagamento 1 e 2
	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(1L);

	    Usuario cliente = new Usuario();
	    cliente.setId(1L);

	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(1L);

	    Cidade cidade = new Cidade();
	    cidade.setId(1L);

	    Endereco endereco = new Endereco();
	    endereco.setCidade(cidade);
	    endereco.setCep("38400-000");
	    endereco.setLogradouro("Rua Teste");
	    endereco.setNumero("123");
	    endereco.setBairro("Centro");

	    ItemPedido item1 = new ItemPedido();
	    item1.setProduto(new Produto());
	    item1.getProduto().setId(1L); // produto do restaurante 1
	    item1.setQuantidade(1);

	    ItemPedido item2 = new ItemPedido();
	    item2.setProduto(new Produto());
	    item2.getProduto().setId(2L); // produto do restaurante 1
	    item2.setQuantidade(2);

	    pedido.setRestaurante(restaurante);
	    pedido.setCliente(cliente);
	    pedido.setFormaPagamento(formaPagamento);
	    pedido.setEnderecoEntrega(endereco);
	    pedido.getItens().add(item1);
	    pedido.getItens().add(item2);

	    Pedido pedidoEmitido = emissaoPedido.emitir(pedido);

	    assertNotNull(pedidoEmitido.getId());
	    assertEquals(new BigDecimal("298.90"), pedidoEmitido.getSubtotal());
	    assertEquals(new BigDecimal("10.00"), pedidoEmitido.getTaxaFrete());
	    assertEquals(new BigDecimal("308.90"), pedidoEmitido.getValorTotal());

	    assertEquals(2, pedidoEmitido.getItens().size());

	    pedidoEmitido.getItens().forEach(item -> {
	        assertNotNull(item.getPedido());
	        assertNotNull(item.getProduto());
	        assertNotNull(item.getPrecoUnitario());
	        assertNotNull(item.getPrecoTotal());
	    });
	}
	
	@Test
	public void deveLancarExcecao_QuandoFormaPagamentoNaoAceita() {
	    Pedido pedido = new Pedido();

	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(1L);

	    Usuario cliente = new Usuario();
	    cliente.setId(1L);

	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(99L); // não associada ao restaurante

	    Cidade cidade = new Cidade();
	    cidade.setId(1L);

	    Endereco endereco = new Endereco();
	    endereco.setCidade(cidade);

	    pedido.setRestaurante(restaurante);
	    pedido.setCliente(cliente);
	    pedido.setFormaPagamento(formaPagamento);
	    pedido.setEnderecoEntrega(endereco);

	    NegocioException ex = assertThrows(
	        NegocioException.class,
	        () -> emissaoPedido.emitir(pedido)
	    );

	    assertEquals("Não existe um cadastro de forma de pagamento com código 99", ex.getMessage());
	}

	
	@Test
	public void deveLancarExcecao_QuandoProdutoNaoPertenceAoRestaurante() {
	    Pedido pedido = new Pedido();

	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(1L);

	    Usuario cliente = new Usuario();
	    cliente.setId(1L);

	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(1L);

	    Cidade cidade = new Cidade();
	    cidade.setId(1L);

	    Endereco endereco = new Endereco();
	    endereco.setCidade(cidade);

	    ItemPedido item = new ItemPedido();
	    item.setProduto(new Produto());
	    item.getProduto().setId(3L); // produto do restaurante 4
	    item.setQuantidade(1);

	    pedido.setRestaurante(restaurante);
	    pedido.setCliente(cliente);
	    pedido.setFormaPagamento(formaPagamento);
	    pedido.setEnderecoEntrega(endereco);
	    pedido.getItens().add(item);

	    EntidadeNaoEncontradaException ex = assertThrows(
	        EntidadeNaoEncontradaException.class,
	        () -> emissaoPedido.emitir(pedido)
	    );

	    assertEquals("Não existe um cadastro de produto com código 3 para o restaurante de código 1", ex.getMessage());
	}
	
	@Test
	public void deveAssociarPedidoNosItens() {
	    Pedido pedido = new Pedido();

	    Restaurante restaurante = new Restaurante();
	    restaurante.setId(4L);

	    Usuario cliente = new Usuario();
	    cliente.setId(1L);

	    FormaPagamento formaPagamento = new FormaPagamento();
	    formaPagamento.setId(2L);

	    Cidade cidade = new Cidade();
	    cidade.setId(1L);

	    Endereco endereco = new Endereco();
	    endereco.setCidade(cidade);
	    endereco.setCep("38400-111");
	    endereco.setLogradouro("Rua Acre");
	    endereco.setNumero("300");
	    endereco.setBairro("Centro");

	    ItemPedido item = new ItemPedido();
	    item.setProduto(new Produto());
	    item.getProduto().setId(3L);
	    item.setQuantidade(1);

	    pedido.setRestaurante(restaurante);
	    pedido.setCliente(cliente);
	    pedido.setFormaPagamento(formaPagamento);
	    pedido.setEnderecoEntrega(endereco);
	    pedido.getItens().add(item);

	    Pedido pedidoEmitido = emissaoPedido.emitir(pedido);

	    ItemPedido itemPersistido = pedidoEmitido.getItens().get(0);

	    assertEquals(pedidoEmitido, itemPersistido.getPedido());
	}
	
}

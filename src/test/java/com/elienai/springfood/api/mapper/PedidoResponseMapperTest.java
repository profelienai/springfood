package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.EnderecoResponse;
import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.api.dto.ItemPedidoResponse;
import com.elienai.springfood.api.dto.PedidoResponse;
import com.elienai.springfood.api.dto.RestauranteResumoResponse;
import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.model.ItemPedido;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.model.StatusPedidoEnum;
import com.elienai.springfood.domain.model.Usuario;

public class PedidoResponseMapperTest {

	private PedidoResponseMapper mapper;
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new PedidoResponseMapper(modelMapper);
	}

	@Test
	void deveConverterPedidoParaPedidoResponse() {
		var estado = new Estado();
		estado.setId(1L);
		estado.setNome("Rio de Janeiro");

		var cidade = new Cidade();
		cidade.setId(1L);
		cidade.setNome("Campo Grande");
		cidade.setEstado(estado);

		var endereco = new Endereco();
		endereco.setCep("38.400-999");
		endereco.setLogradouro("Rua João Pinheiro");
		endereco.setNumero("1000");
		endereco.setComplemento("C1");
		endereco.setBairro("Centro");
		endereco.setCidade(cidade);

		var restaurante = new Restaurante();
		restaurante.setId(1L);
		restaurante.setNome("Borbulha");

		var cliente = new Usuario();
		cliente.setId(10L);
		cliente.setNome("João Silva");
		cliente.setEmail("joao@email.com");

		var formaPagamento = new FormaPagamento();
		formaPagamento.setId(5L);
		formaPagamento.setDescricao("Cartão de Crédito");

		var itemPedido = new ItemPedido();
		itemPedido.setId(1L);
		itemPedido.setQuantidade(2);
		itemPedido.setPrecoUnitario(new BigDecimal("25.00"));
		itemPedido.setPrecoTotal(new BigDecimal("50.00"));
		itemPedido.setObservacao("Sem cebola");

		var produto = new Produto();
		produto.setId(100L);
		produto.setNome("Hambúrguer Artesanal");
		itemPedido.setProduto(produto);

		var pedido = new Pedido();
		pedido.setId(99L);
		pedido.setTaxaFrete(new BigDecimal("10.00"));
		pedido.setEnderecoEntrega(endereco);
		pedido.setRestaurante(restaurante);
		pedido.setCliente(cliente);
		pedido.setFormaPagamento(formaPagamento);
		pedido.setStatus(StatusPedidoEnum.CONFIRMADO);
		pedido.setDataCriacao(OffsetDateTime.now());
		pedido.setItens(List.of(itemPedido));
		pedido.calcularValorTotal();

		var pedidoResponse = mapper.toResponse(pedido);

		assertThat(pedidoResponse)
			.isNotNull()
			.extracting(
				PedidoResponse::getId,
				PedidoResponse::getSubtotal,
				PedidoResponse::getTaxaFrete,
				PedidoResponse::getValorTotal,
				PedidoResponse::getStatus
			)
			.containsExactly(
				99L,
				new BigDecimal("50.00"),
				new BigDecimal("10.00"),
				new BigDecimal("60.00"),
				"CONFIRMADO"
			);

		assertThat(pedidoResponse.getRestaurante())
			.isNotNull()
			.extracting(
				RestauranteResumoResponse::getId,
				RestauranteResumoResponse::getNome
			)
			.containsExactly(1L, "Borbulha");

		assertThat(pedidoResponse.getCliente())
			.isNotNull()
			.extracting(
				UsuarioResponse::getId,
				UsuarioResponse::getNome,
				UsuarioResponse::getEmail
			)
			.containsExactly(10L, "João Silva", "joao@email.com");

		assertThat(pedidoResponse.getFormaPagamento())
			.isNotNull()
			.extracting(
				FormaPagamentoResponse::getId,
				FormaPagamentoResponse::getDescricao
			)
			.containsExactly(5L, "Cartão de Crédito");

		assertThat(pedidoResponse.getEnderecoEntrega())
			.isNotNull()
			.extracting(
				EnderecoResponse::getCep,
				EnderecoResponse::getLogradouro,
				EnderecoResponse::getNumero,
				EnderecoResponse::getComplemento,
				EnderecoResponse::getBairro,
				EnderecoResponse::getCidade,
				EnderecoResponse::getEstado
			)
			.containsExactly(
				"38.400-999",
				"Rua João Pinheiro",
				"1000",
				"C1",
				"Centro",
				"Campo Grande",
				"Rio de Janeiro"
			);

		assertThat(pedidoResponse.getItens())
			.hasSize(1)
			.extracting(
				ItemPedidoResponse::getProdutoId,
				ItemPedidoResponse::getProdutoNome,
				ItemPedidoResponse::getQuantidade,
				ItemPedidoResponse::getPrecoUnitario,
				ItemPedidoResponse::getPrecoTotal,
				ItemPedidoResponse::getObservacao
			)
			.containsExactly(
				tuple(
					100L,
					"Hambúrguer Artesanal",
					2,
					new BigDecimal("25.00"),
					new BigDecimal("50.00"),
					"Sem cebola"
				)
			);
	}

	@Test
	void deveConverterCollectionDePedidoParaCollectionDePedidoResponse() {
		var pedido1 = new Pedido();
		pedido1.setId(1L);
		pedido1.setSubtotal(new BigDecimal("100.00"));
		pedido1.setTaxaFrete(new BigDecimal("10.00"));
		pedido1.setValorTotal(new BigDecimal("110.00"));
		pedido1.setStatus(StatusPedidoEnum.CRIADO);

		var pedido2 = new Pedido();
		pedido2.setId(2L);
		pedido2.setSubtotal(new BigDecimal("200.00"));
		pedido2.setTaxaFrete(new BigDecimal("20.00"));
		pedido2.setValorTotal(new BigDecimal("220.00"));
		pedido2.setStatus(StatusPedidoEnum.CANCELADO);

		var pedidos = List.of(pedido1, pedido2);

		var pedidosResponse = mapper.toCollectionResponse(pedidos);

		assertThat(pedidosResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(
				PedidoResponse::getId,
				PedidoResponse::getSubtotal,
				PedidoResponse::getTaxaFrete,
				PedidoResponse::getValorTotal,
				PedidoResponse::getStatus
			)
			.containsExactlyInAnyOrder(
				tuple(1L, new BigDecimal("100.00"), new BigDecimal("10.00"), new BigDecimal("110.00"), "CRIADO"),
				tuple(2L, new BigDecimal("200.00"), new BigDecimal("20.00"), new BigDecimal("220.00"), "CANCELADO")
			);
	}
}

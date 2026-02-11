package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.PedidoResumoResponse;
import com.elienai.springfood.api.dto.RestauranteResumoResponse;
import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.model.Usuario;

public class PedidoResumoResponseMapperTest {

	private PedidoResumoResponseMapper mapper;
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new PedidoResumoResponseMapper(modelMapper);
	}

	@Test
	void deveConverterPedidoParaPedidoResumoResponse() {
		var restaurante = new Restaurante();
		restaurante.setId(1L);
		restaurante.setNome("Borbulha");

		var cliente = new Usuario();
		cliente.setId(10L);
		cliente.setNome("João Silva");
		cliente.setEmail("joao@email.com");

		var pedido = new Pedido();
		pedido.setId(1L);
		pedido.setCodigo("ABC123");
		pedido.setSubtotal(new BigDecimal("100.00"));
		pedido.setTaxaFrete(new BigDecimal("10.00"));
		pedido.setValorTotal(new BigDecimal("110.00"));
		pedido.setDataCriacao(OffsetDateTime.now());
		pedido.setRestaurante(restaurante);
		pedido.setCliente(cliente);

		var pedidoResumoResponse = mapper.toResponse(pedido);

		assertThat(pedidoResumoResponse)
			.isNotNull()
			.extracting(
				PedidoResumoResponse::getCodigo,
				PedidoResumoResponse::getSubtotal,
				PedidoResumoResponse::getTaxaFrete,
				PedidoResumoResponse::getValorTotal,
				PedidoResumoResponse::getStatus
			)
			.containsExactly(
				"ABC123",
				new BigDecimal("100.00"),
				new BigDecimal("10.00"),
				new BigDecimal("110.00"),
				"CRIADO"
			);

		assertThat(pedidoResumoResponse.getRestaurante())
			.isNotNull()
			.extracting(
				RestauranteResumoResponse::getId,
				RestauranteResumoResponse::getNome
			)
			.containsExactly(1L, "Borbulha");

		assertThat(pedidoResumoResponse.getCliente())
			.isNotNull()
			.extracting(
				UsuarioResponse::getId,
				UsuarioResponse::getNome,
				UsuarioResponse::getEmail
			)
			.containsExactly(10L, "João Silva", "joao@email.com");
	}

	@Test
	void deveConverterCollectionDePedidoParaCollectionDePedidoResumoResponse() {
		var pedido1 = new Pedido();
		pedido1.setId(1L);
		pedido1.setCodigo("ABC123");
		pedido1.setSubtotal(new BigDecimal("50.00"));
		pedido1.setTaxaFrete(new BigDecimal("5.00"));
		pedido1.setValorTotal(new BigDecimal("55.00"));

		var pedido2 = new Pedido();
		pedido2.setId(2L);
		pedido2.setCodigo("ABC234");
		pedido2.setSubtotal(new BigDecimal("200.00"));
		pedido2.setTaxaFrete(new BigDecimal("20.00"));
		pedido2.setValorTotal(new BigDecimal("220.00"));

		var pedidos = List.of(pedido1, pedido2);

		var pedidosResumoResponse = mapper.toCollectionResponse(pedidos);

		assertThat(pedidosResumoResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(
				PedidoResumoResponse::getCodigo,
				PedidoResumoResponse::getSubtotal,
				PedidoResumoResponse::getTaxaFrete,
				PedidoResumoResponse::getValorTotal,
				PedidoResumoResponse::getStatus
			)
			.containsExactlyInAnyOrder(
				tuple("ABC123", new BigDecimal("50.00"), new BigDecimal("5.00"), new BigDecimal("55.00"), "CRIADO"),
				tuple("ABC234", new BigDecimal("200.00"), new BigDecimal("20.00"), new BigDecimal("220.00"), "CRIADO")
			);
	}
}

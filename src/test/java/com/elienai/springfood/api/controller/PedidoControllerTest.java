package com.elienai.springfood.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.api.dto.EnderecoResponse;
import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.api.dto.ItemPedidoResponse;
import com.elienai.springfood.api.dto.PedidoResponse;
import com.elienai.springfood.api.dto.PedidoResumoResponse;
import com.elienai.springfood.api.dto.RestauranteResumoResponse;
import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.api.mapper.PedidoResponseMapper;
import com.elienai.springfood.api.mapper.PedidoResumoResponseMapper;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.repository.PedidoRepository;
import com.elienai.springfood.domain.service.EmissaoPedidoService;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PedidoRepository pedidoRepository;
	
	@MockBean
	private EmissaoPedidoService emissaoPedido;

	@MockBean
	private PedidoResponseMapper pedidoResponseMapper;
	
	@MockBean
	private PedidoResumoResponseMapper pedidoResumoResponseMapper;

	private Pedido pedido1;
	private Pedido pedido2;

	private PedidoResponse pedidoResponse1;
	
	private PedidoResumoResponse pedidoResumoResponse1;
	private PedidoResumoResponse pedidoResumoResponse2;
	
	@BeforeEach
	private void setUp() {
		prepararDados();
	}
	
	@Test
	public void deveListarPedidos() throws Exception {
		List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
		List<PedidoResumoResponse> responses = Arrays.asList(pedidoResumoResponse1, pedidoResumoResponse2);
		
		when(pedidoRepository.findAll()).thenReturn(pedidos);
		when(pedidoResumoResponseMapper.toCollectionResponse(pedidos)).thenReturn(responses);
		
		mockMvc.perform(get("/pedidos")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))

				// Pedido 1
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].subtotal").value(100.00))
				.andExpect(jsonPath("$[0].taxaFrete").value(10.00))
				.andExpect(jsonPath("$[0].valorTotal").value(110.00))
				.andExpect(jsonPath("$[0].status").value("Criado"))
				.andExpect(jsonPath("$[0].dataCriacao").value("2025-01-01T10:00:00Z"))
				.andExpect(jsonPath("$[0].cliente.id").value(10))
				.andExpect(jsonPath("$[0].cliente.nome").value("João"))
				.andExpect(jsonPath("$[0].cliente.email").value("joao@email.com"))
				.andExpect(jsonPath("$[0].restaurante.id").value(100))
				.andExpect(jsonPath("$[0].restaurante.nome").value("Restaurante A"))

				// Pedido 2
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].subtotal").value(200.00))
				.andExpect(jsonPath("$[1].taxaFrete").value(20.00))
				.andExpect(jsonPath("$[1].valorTotal").value(220.00))
				.andExpect(jsonPath("$[1].status").value("Confirmado"))
				.andExpect(jsonPath("$[1].dataCriacao").value("2025-01-02T11:00:00Z"))
				.andExpect(jsonPath("$[1].cliente.id").value(11))
				.andExpect(jsonPath("$[1].cliente.nome").value("Maria"))
				.andExpect(jsonPath("$[1].cliente.email").value("maria@email.com"))
				.andExpect(jsonPath("$[1].restaurante.id").value(101))
				.andExpect(jsonPath("$[1].restaurante.nome").value("Restaurante B"));			   
			   
	}
	
	@Test
	public void deveBuscarPedidoPorId() throws Exception {
		when(emissaoPedido.buscarOuFalhar(1L)).thenReturn(pedido1);
		when(pedidoResponseMapper.toResponse(pedido1)).thenReturn(pedidoResponse1);
		
		mockMvc.perform(get("/pedidos/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())

			// Dados principais
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.subtotal").value(60.00))
			.andExpect(jsonPath("$.taxaFrete").value(10.00))
			.andExpect(jsonPath("$.valorTotal").value(70.00))
			.andExpect(jsonPath("$.status").value("Criado"))
			.andExpect(jsonPath("$.dataCriacao").value("2025-01-01T10:00:00Z"))
			.andExpect(jsonPath("$.dataConfirmacao").doesNotExist())
			.andExpect(jsonPath("$.dataEntrega").doesNotExist())
			.andExpect(jsonPath("$.dataCancelamento").doesNotExist())

			// Cliente
			.andExpect(jsonPath("$.cliente.id").value(10))
			.andExpect(jsonPath("$.cliente.nome").value("João"))
			.andExpect(jsonPath("$.cliente.email").value("joao@email.com"))

			// Restaurante
			.andExpect(jsonPath("$.restaurante.id").value(100))
			.andExpect(jsonPath("$.restaurante.nome").value("Restaurante A"))

			// Forma de pagamento
			.andExpect(jsonPath("$.formaPagamento.id").value(1))
			.andExpect(jsonPath("$.formaPagamento.descricao").value("Cartão de Crédito"))

			// Endereço de entrega
			.andExpect(jsonPath("$.enderecoEntrega.cep").value("12345-000"))
			.andExpect(jsonPath("$.enderecoEntrega.logradouro").value("Rua das Flores"))
			.andExpect(jsonPath("$.enderecoEntrega.numero").value("100"))
			.andExpect(jsonPath("$.enderecoEntrega.complemento").value("Apto 10"))
			.andExpect(jsonPath("$.enderecoEntrega.bairro").value("Centro"))
			.andExpect(jsonPath("$.enderecoEntrega.cidade").value("São Paulo"))
			.andExpect(jsonPath("$.enderecoEntrega.estado").value("SP"))

			// Itens
			.andExpect(jsonPath("$.itens", hasSize(1)))
			.andExpect(jsonPath("$.itens[0].produtoId").value(50))
			.andExpect(jsonPath("$.itens[0].produtoNome").value("Pizza"))
			.andExpect(jsonPath("$.itens[0].quantidade").value(2))
			.andExpect(jsonPath("$.itens[0].precoUnitario").value(30.00))
			.andExpect(jsonPath("$.itens[0].precoTotal").value(60.00))
			.andExpect(jsonPath("$.itens[0].observacao").value("Sem cebola"));
	}
	
	private void prepararDados() {
		pedido1 = new Pedido();
		pedido1.setId(1L);
		
		pedido2 = new Pedido();
		pedido2.setId(2L);
		
		UsuarioResponse cliente1 = new UsuarioResponse();
		cliente1.setId(10L);
		cliente1.setNome("João");
		cliente1.setEmail("joao@email.com");

		UsuarioResponse cliente2 = new UsuarioResponse();
		cliente2.setId(11L);
		cliente2.setNome("Maria");
		cliente2.setEmail("maria@email.com");

		RestauranteResumoResponse restaurante1 = new RestauranteResumoResponse();
		restaurante1.setId(100L);
		restaurante1.setNome("Restaurante A");

		RestauranteResumoResponse restaurante2 = new RestauranteResumoResponse();
		restaurante2.setId(101L);
		restaurante2.setNome("Restaurante B");

		pedidoResumoResponse1 = new PedidoResumoResponse();
		pedidoResumoResponse1.setId(1L);
		pedidoResumoResponse1.setSubtotal(new BigDecimal("100.00"));
		pedidoResumoResponse1.setTaxaFrete(new BigDecimal("10.00"));
		pedidoResumoResponse1.setValorTotal(new BigDecimal("110.00"));
		pedidoResumoResponse1.setStatus("Criado");
		pedidoResumoResponse1.setDataCriacao(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
		pedidoResumoResponse1.setCliente(cliente1);
		pedidoResumoResponse1.setRestaurante(restaurante1);

		pedidoResumoResponse2 = new PedidoResumoResponse();
		pedidoResumoResponse2.setId(2L);
		pedidoResumoResponse2.setSubtotal(new BigDecimal("200.00"));
		pedidoResumoResponse2.setTaxaFrete(new BigDecimal("20.00"));
		pedidoResumoResponse2.setValorTotal(new BigDecimal("220.00"));
		pedidoResumoResponse2.setStatus("Confirmado");
		pedidoResumoResponse2.setDataCriacao(OffsetDateTime.parse("2025-01-02T11:00:00Z"));
		pedidoResumoResponse2.setCliente(cliente2);
		pedidoResumoResponse2.setRestaurante(restaurante2);
		
		FormaPagamentoResponse formaPagamento = new FormaPagamentoResponse();
		formaPagamento.setId(1L);
		formaPagamento.setDescricao("Cartão de Crédito");

		EnderecoResponse endereco = new EnderecoResponse();
		endereco.setCep("12345-000");
		endereco.setLogradouro("Rua das Flores");
		endereco.setNumero("100");
		endereco.setComplemento("Apto 10");
		endereco.setBairro("Centro");
		endereco.setCidade("São Paulo");
		endereco.setEstado("SP");

		ItemPedidoResponse item = new ItemPedidoResponse();
		item.setProdutoId(50L);
		item.setProdutoNome("Pizza");
		item.setQuantidade(2);
		item.setPrecoUnitario(new BigDecimal("30.00"));
		item.setPrecoTotal(new BigDecimal("60.00"));
		item.setObservacao("Sem cebola");

		pedidoResponse1 = new PedidoResponse();
		pedidoResponse1.setId(1L);
		pedidoResponse1.setSubtotal(new BigDecimal("60.00"));
		pedidoResponse1.setTaxaFrete(new BigDecimal("10.00"));
		pedidoResponse1.setValorTotal(new BigDecimal("70.00"));
		pedidoResponse1.setStatus("Criado");
		pedidoResponse1.setDataCriacao(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
		pedidoResponse1.setDataConfirmacao(null);
		pedidoResponse1.setDataEntrega(null);
		pedidoResponse1.setDataCancelamento(null);
		pedidoResponse1.setCliente(cliente1);
		pedidoResponse1.setRestaurante(restaurante1);
		pedidoResponse1.setFormaPagamento(formaPagamento);
		pedidoResponse1.setEnderecoEntrega(endereco);
		pedidoResponse1.setItens(List.of(item));		
	}
}

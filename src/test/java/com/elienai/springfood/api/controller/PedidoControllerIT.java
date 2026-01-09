package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.util.DatabaseCleaner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/EmissaoPedidoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PedidoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;

	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/pedidos";
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarPedidos() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    

			// Pedido 1
			.body("[0].id", is(1))
			.body("[0].status", is("CRIADO"))
			.body("[0].valorTotal", is(308.90f))
			.body("[0].restaurante.nome", is("Thai Gourmet"))
			.body("[0].cliente.nome", is("João da Silva"))
	
			// Pedido 2
			.body("[1].id", is(2))
			.body("[1].status", is("CRIADO"))
			.body("[1].valorTotal", is(79f))
			.body("[1].restaurante.nome", is("Java Steakhouse"))
			.body("[1].cliente.nome", is("João da Silva"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarPedidoExistente() {
		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{pedidoId}")
		.then()
			.statusCode(HttpStatus.OK.value())

			// Dados principais
			.body("id", is(1))
			.body("status", is("CRIADO"))
			.body("subtotal", is(298.90f))
			.body("taxaFrete", is(10f))
			.body("valorTotal", is(308.90f))

			// Restaurante
			.body("restaurante.id", is(1))
			.body("restaurante.nome", is("Thai Gourmet"))

			// Cliente
			.body("cliente.id", is(1))
			.body("cliente.nome", is("João da Silva"))
			.body("cliente.email", is("joao.ger@gmail.com"))

			// Forma de pagamento
			.body("formaPagamento.id", is(1))
			.body("formaPagamento.descricao", is("Cartão de crédito"))

			// Endereço de entrega
			.body("enderecoEntrega.cep", is("38400-000"))
			.body("enderecoEntrega.logradouro", is("Rua Floriano Peixoto"))
			.body("enderecoEntrega.numero", is("500"))
			.body("enderecoEntrega.complemento", is("Apto 801"))
			.body("enderecoEntrega.bairro", is("Brasil"))
			.body("enderecoEntrega.cidade", is("Uberlândia"))
			.body("enderecoEntrega.estado", is("Minas Gerais"))

			// Itens
			.body("itens", hasSize(2))
			.body("itens[0].produtoId", is(1))
			.body("itens[0].produtoNome", is("Porco com molho agridoce"))
			.body("itens[0].quantidade", is(1))
			.body("itens[0].precoUnitario", is(78.9f))
			.body("itens[0].precoTotal", is(78.9f))
			.body("itens[0].observacao", nullValue())

			.body("itens[1].produtoId", is(2))
			.body("itens[1].produtoNome", is("Camarão tailandês"))
			.body("itens[1].quantidade", is(2))
			.body("itens[1].precoUnitario", is(110f))
			.body("itens[1].precoTotal", is(220f))
			.body("itens[1].observacao", is("Menos picante, por favor"));
	}
	
	@Test
	public void deveRetornarStatus404_QuandoConsultarPedidoInexistente() {
		given()
			.pathParam("pedidoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{pedidoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um pedido com código 99"))
	        .body("userMessage", is("Não existe um pedido com código 99"));			
	}	
}

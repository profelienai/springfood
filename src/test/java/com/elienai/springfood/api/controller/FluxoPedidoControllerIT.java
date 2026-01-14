package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
@Sql(
	scripts = "/sql/EmissaoPedidoServiceIT.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class FluxoPedidoControllerIT {

	@LocalServerPort
	private int port;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@BeforeEach
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/pedidos";
	}

	@AfterEach
	void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	// =====================================================
	// CONFIRMAÇÃO
	// =====================================================

	@Test
	public void deveRetornarStatus204_QuandoConfirmarPedidoCriado() {
		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.put("/{pedidoId}/confirmacao")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// valida estado final
		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{pedidoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("status", is("CONFIRMADO"))
			.body("dataConfirmacao", notNullValue())
			.body("dataCancelamento", nullValue())
			.body("dataEntrega", nullValue());
	}

	@Test
	public void deveRetornarStatus400_QuandoConfirmarPedidoJaConfirmado() {
		// confirma uma vez
		given()
			.pathParam("pedidoId", 1L)
		.when()
			.put("/{pedidoId}/confirmacao")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// tenta confirmar novamente
		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.put("/{pedidoId}/confirmacao")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
	        .body("status", is(HttpStatus.BAD_REQUEST.value()))
	        .body("title", is("Violação de regra de negócio"))
	        .body("detail", is("Status do pedido 1 não pode ser alterado de Confirmado para Confirmado"))
	        .body("userMessage", is("Status do pedido 1 não pode ser alterado de Confirmado para Confirmado"));			
	}

	// =====================================================
	// CANCELAMENTO
	// =====================================================

	@Test
	public void deveRetornarStatus204_QuandoCancelarPedidoCriado() {
		given()
			.pathParam("pedidoId", 2L)
			.accept(ContentType.JSON)
		.when()
			.put("/{pedidoId}/cancelamento")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());

		given()
			.pathParam("pedidoId", 2L)
			.accept(ContentType.JSON)
		.when()
			.get("/{pedidoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("status", is("CANCELADO"))
			.body("dataCancelamento", notNullValue())
			.body("dataConfirmacao", nullValue())
			.body("dataEntrega", nullValue());
	}

	@Test
	public void deveRetornarStatus400_QuandoCancelarPedidoJaConfirmado() {
		// confirma primeiro
		given()
			.pathParam("pedidoId", 1L)
		.when()
			.put("/{pedidoId}/confirmacao")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// tenta cancelar
		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.put("/{pedidoId}/cancelamento")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
	        .body("status", is(HttpStatus.BAD_REQUEST.value()))
	        .body("title", is("Violação de regra de negócio"))
	        .body("detail", is("Status do pedido 1 não pode ser alterado de Confirmado para Cancelado"))
	        .body("userMessage", is("Status do pedido 1 não pode ser alterado de Confirmado para Cancelado"));
	}

	// =====================================================
	// ENTREGA
	// =====================================================

	@Test
	public void deveRetornarStatus204_QuandoEntregarPedidoConfirmado() {
		// confirma primeiro
		given()
			.pathParam("pedidoId", 1L)
		.when()
			.put("/{pedidoId}/confirmacao")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// entrega
		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.put("/{pedidoId}/entrega")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());

		given()
			.pathParam("pedidoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{pedidoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("status", is("ENTREGUE"))
			.body("dataEntrega", notNullValue());
	}

	@Test
	public void deveRetornarStatus400_QuandoEntregarPedidoCriado() {
		given()
			.pathParam("pedidoId", 2L)
			.accept(ContentType.JSON)
		.when()
			.put("/{pedidoId}/entrega")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
	        .body("status", is(HttpStatus.BAD_REQUEST.value()))
	        .body("title", is("Violação de regra de negócio"))
	        .body("detail", is("Status do pedido 2 não pode ser alterado de Criado para Entregue"))
	        .body("userMessage", is("Status do pedido 2 não pode ser alterado de Criado para Entregue"));			
	}
}

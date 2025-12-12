package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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
@Sql(scripts = "/sql/CadastroRestauranteServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RestauranteFormaPagamentoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes";
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarFormasPagamento() {
		given()
			.pathParam("restauranteId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{restauranteId}/formas-pagamento")
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("descricao", hasItems("Cartão de débito", "Dinheiro"));
	}
	
	@Test
	public void deveRetornarStatus204_QuandoAssociarFormaPagamento() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("formaPagamentoId", 1L)
		.when()
			.put("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}	

	@Test
	public void deveRetornarStatus204_QuandoDesassociarFormaPagamento() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("formaPagamentoId", 3L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoInformarRestauranteInexistenteNaAssociacao() {
		given()
			.pathParam("restauranteId", 99L)
			.pathParam("formaPagamentoId", 1L)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de restaurante com código 99"))
			.body("userMessage", is("Não existe um cadastro de restaurante com código 99"));	
	}		

	@Test
	public void deveRetornarStatus404_QuandoInformarRestauranteInexistenteNaDesassociacao() {
		given()
			.pathParam("restauranteId", 99L)
			.pathParam("formaPagamentoId", 1L)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de restaurante com código 99"))
			.body("userMessage", is("Não existe um cadastro de restaurante com código 99"));	
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoAssociarFormaPagamentoInexistente() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("formaPagamentoId", 99L)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de forma de pagamento com código 99"))
			.body("userMessage", is("Não existe um cadastro de forma de pagamento com código 99"));	
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoDesassociarFormaPagamentoInexistente() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("formaPagamentoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{restauranteId}/formas-pagamento/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de forma de pagamento com código 99"))
			.body("userMessage", is("Não existe um cadastro de forma de pagamento com código 99"));		
	}
	
}

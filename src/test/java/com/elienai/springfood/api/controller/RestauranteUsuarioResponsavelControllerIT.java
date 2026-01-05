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
@Sql(
	scripts = "/sql/CadastroRestauranteServiceIT.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class RestauranteUsuarioResponsavelControllerIT {

	@LocalServerPort
	private int port;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes/{restauranteId}/responsaveis";
	}

	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarResponsaveisDoRestaurante() {
		given()
			.pathParam("restauranteId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("", hasSize(1))
			.body("nome", hasItems("João da Silva"));
	}

	@Test
	public void deveRetornarStatus204_QuandoAssociarResponsavelAoRestaurante() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("usuarioId", 2L)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}

	@Test
	public void deveRetornarStatus204_QuandoDesassociarResponsavelDoRestaurante() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("usuarioId", 1L)
		.when()
			.delete("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}

	@Test
	public void deveRetornarStatus404_QuandoConsultarRestauranteInexistente() {
		given()
			.pathParam("restauranteId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de restaurante com código 99"))
			.body("userMessage", is("Não existe um cadastro de restaurante com código 99"));
	}

	@Test
	public void deveRetornarStatus404_QuandoAssociarUsuarioInexistente() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("usuarioId", 99L)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de usuário com código 99"))
			.body("userMessage", is("Não existe um cadastro de usuário com código 99"));
	}

	@Test
	public void deveRetornarStatus404_QuandoDesassociarUsuarioInexistente() {
		given()
			.pathParam("restauranteId", 1L)
			.pathParam("usuarioId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de usuário com código 99"))
			.body("userMessage", is("Não existe um cadastro de usuário com código 99"));
	}

	@Test
	public void deveRetornarStatus404_QuandoAssociarResponsavelARestauranteInexistente() {
		given()
			.pathParam("restauranteId", 99L)
			.pathParam("usuarioId", 1L)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de restaurante com código 99"))
			.body("userMessage", is("Não existe um cadastro de restaurante com código 99"));
	}
}

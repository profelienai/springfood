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
	scripts = "/sql/CadastroUsuarioServiceIT.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class UsuarioGrupoControllerIT {

	@LocalServerPort
	private int port;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/usuarios/{usuarioId}/grupos";
	}

	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarGruposDoUsuario() {
		given()
			.pathParam("usuarioId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("", hasSize(1))
			.body("nome", hasItems("Gerente"));
	}

	@Test
	public void deveRetornarStatus204_QuandoAssociarGrupoAoUsuario() {
		given()
			.pathParam("usuarioId", 1L)
			.pathParam("grupoId", 2L)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}

	@Test
	public void deveRetornarStatus204_QuandoDesassociarGrupoDoUsuario() {
		given()
			.pathParam("usuarioId", 1L)
			.pathParam("grupoId", 1L)
		.when()
			.delete("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}

	@Test
	public void deveRetornarStatus404_QuandoConsultarUsuarioInexistente() {
		given()
			.pathParam("usuarioId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de usuário com código 99"))
			.body("userMessage", is("Não existe um cadastro de usuário com código 99"));
	}

	@Test
	public void deveRetornarStatus404_QuandoAssociarGrupoInexistente() {
		given()
			.pathParam("usuarioId", 1L)
			.pathParam("grupoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de grupo com código 99"))
			.body("userMessage", is("Não existe um cadastro de grupo com código 99"));
	}

	@Test
	public void deveRetornarStatus404_QuandoDesassociarGrupoInexistente() {
		given()
			.pathParam("usuarioId", 1L)
			.pathParam("grupoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de grupo com código 99"))
			.body("userMessage", is("Não existe um cadastro de grupo com código 99"));
	}

	@Test
	public void deveRetornarStatus404_QuandoAssociarGrupoAUsuarioInexistente() {
		given()
			.pathParam("usuarioId", 99L)
			.pathParam("grupoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type", is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de usuário com código 99"))
			.body("userMessage", is("Não existe um cadastro de usuário com código 99"));
	}
}

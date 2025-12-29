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
@Sql(scripts = "/sql/CadastroGrupoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class GrupoPermissaoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/grupos/{grupoId}/permissoes";
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarPermissoes() {
		given()
			.pathParam("grupoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(1))
	    	.body("nome", hasItems("CONSULTAR_COZINHAS"))
	    	.body("descricao", hasItems("Permite consultar cozinhas"));
	}
	
	@Test
	public void deveRetornarStatus204_QuandoAssociarPermissao() {
		given()
			.pathParam("grupoId", 1L)
			.pathParam("permissaoId", 2L)
		.when()
			.put("/{permissaoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}	

	@Test
	public void deveRetornarStatus204_QuandoDesassociarPermissao() {
		given()
			.pathParam("grupoId", 1L)
			.pathParam("permissaoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{permissaoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoInformarGrupoInexistenteNaAssociacao() {
		given()
			.pathParam("grupoId", 99L)
			.pathParam("permissaoId", 1L)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{permissaoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de grupo com código 99"))
			.body("userMessage", is("Não existe um cadastro de grupo com código 99"));	
	}		

	@Test
	public void deveRetornarStatus404_QuandoInformarGrupoInexistenteNaDesassociacao() {
		given()
			.pathParam("grupoId", 99L)
			.pathParam("permissaoId", 1L)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.delete("/{permissaoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de grupo com código 99"))
			.body("userMessage", is("Não existe um cadastro de grupo com código 99"));	
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoAssociarPermissaoInexistente() {
		given()
			.pathParam("grupoId", 1L)
			.pathParam("permissaoId", 99L)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{permissaoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de permissão com código 99"))
			.body("userMessage", is("Não existe um cadastro de permissão com código 99"));	
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoDesassociarPermissaoInexistente() {
		given()
			.pathParam("grupoId", 1L)
			.pathParam("permissaoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{permissaoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de permissão com código 99"))
			.body("userMessage", is("Não existe um cadastro de permissão com código 99"));		
	}
	
}

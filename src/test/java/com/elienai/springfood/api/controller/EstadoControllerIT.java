package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
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
import com.elienai.springfood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroEstadoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EstadoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonEstadoSP;
	private String jsonEstadoComDadosInvalidos;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/estados";

		jsonEstadoSP = ResourceUtils.getContentFromResource(
				"/json/correto/estado-sp.json");
		
		jsonEstadoComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/correto/estado-com-dados-invalidos.json");
		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarEstados() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("nome", hasItems("Rio de Janeiro", "Minas Gerais"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarEstadoExistente() {
		given()
			.pathParam("estadoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{estadoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Rio de Janeiro"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarEstado() {
		given()
			.body(jsonEstadoSP)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", is(3))
			.body("nome", is("São Paulo"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarEstadoExistente() {
		given()
			.pathParam("estadoId", 1L)
			.body(jsonEstadoSP)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{estadoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("São Paulo"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoRemoverEstadoExistente() {
		given()
			.pathParam("estadoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{estadoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	

	@Test
	public void deveRetornarStatus404_QuandoConsultarEstadoInexistente() {
		given()
			.pathParam("estadoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{estadoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de estado com código 99"))
	        .body("userMessage", is("Não existe um cadastro de estado com código 99"));			
	}	

	@Test
	public void deveRetornarStatus400_QuandoAdicionarEstadoComDadosInvalidos() {
		given()
			.body(jsonEstadoComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("nome"))
			.body("objects[0].userMessage", is("Nome do estado é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoAlterarEstadoInexistente() {
		given()
			.pathParam("estadoId", 99L)
			.body(jsonEstadoSP)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{estadoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de estado com código 99"))
			.body("userMessage", is("Não existe um cadastro de estado com código 99"));	
	}	

	@Test
	public void deveRetornarStatus400_QuandoAlterarEstadoComDadosInvalidos() {
		given()
			.pathParam("estadoId", 99L)
			.body(jsonEstadoComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{estadoId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("nome"))
			.body("objects[0].userMessage", is("Nome do estado é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoRemoverEstadoInexistente() {
		given()
			.pathParam("estadoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{estadoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de estado com código 99"))
			.body("userMessage", is("Não existe um cadastro de estado com código 99"));		
	}
	
	@Test
	public void deveRetornarStatus409_QuandoRemoverEstadoEmUso() {
		given()
			.pathParam("estadoId", 2L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{estadoId}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value())
	        .body("status", is(409))
	        .body("type", is("https://springfood.com.br/entidade-em-uso"))
	        .body("title", is("Entidade em uso"))
	        .body("detail", is("Estado de código 2 não pode ser removido, pois está em uso"))
	        .body("userMessage", is("Estado de código 2 não pode ser removido, pois está em uso"));			;
	}	
}

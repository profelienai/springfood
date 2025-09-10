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
@Sql(scripts = "/sql/CadastroCozinhaServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CozinhaControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonCozinhaBrasileira;
	private String jsonCozinhaComDadosInvalidos;
	private String jsonCozinhaComAtributoInexistente;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";

		jsonCozinhaBrasileira = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-brasileira.json");
		
		jsonCozinhaComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/cozinha-com-dados-invalidos.json");

		jsonCozinhaComAtributoInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/cozinha-com-atributo-inexistente.json");		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarCozinhas() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("nome", hasItems("Italiana", "Arabe"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarCozinhaExistente() {
		given()
			.pathParam("cozinhaId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Italiana"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarCozinha() {
		given()
			.body(jsonCozinhaBrasileira)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", is(3))
			.body("nome", is("Brasileira"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarCozinhaExistente() {
		given()
			.pathParam("cozinhaId", 1L)
			.body(jsonCozinhaBrasileira)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Brasileira"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoRemoverCozinhaExistente() {
		given()
			.pathParam("cozinhaId", 1L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	

	@Test
	public void deveRetornarStatus404_QuandoConsultarCozinhaInexistente() {
		given()
			.pathParam("cozinhaId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de cozinha com código 99"))
	        .body("userMessage", is("Não existe um cadastro de cozinha com código 99"));			
	}	

	@Test
	public void deveRetornarStatus400_QuandoAdicionarCozinhaComDadosInvalidos() {
		given()
			.body(jsonCozinhaComDadosInvalidos)
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
			.body("objects[0].userMessage", is("Nome da cozinha é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarCozinhaComAtributoInexistente() {
		given()
			.body(jsonCozinhaComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'atr_inexistente' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));		
	}		
	
	@Test
	public void deveRetornarStatus404_QuandoAlterarCozinhaInexistente() {
		given()
			.pathParam("cozinhaId", 99L)
			.body(jsonCozinhaBrasileira)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de cozinha com código 99"))
			.body("userMessage", is("Não existe um cadastro de cozinha com código 99"));	
	}	

	@Test
	public void deveRetornarStatus400_QuandoAlterarCozinhaComDadosInvalidos() {
		given()
			.pathParam("cozinhaId", 99L)
			.body(jsonCozinhaComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("nome"))
			.body("objects[0].userMessage", is("Nome da cozinha é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarCozinhaComAtributoInexistente() {
		given()
			.pathParam("cozinhaId", 99L)
			.body(jsonCozinhaComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'atr_inexistente' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));		
	}		
	
	@Test
	public void deveRetornarStatus404_QuandoRemoverCozinhaInexistente() {
		given()
			.pathParam("cozinhaId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de cozinha com código 99"))
			.body("userMessage", is("Não existe um cadastro de cozinha com código 99"));		
	}
	
	@Test
	public void deveRetornarStatus409_QuandoRemoverCozinhaEmUso() {
		given()
			.pathParam("cozinhaId", 2L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value())
	        .body("status", is(409))
	        .body("type", is("https://springfood.com.br/entidade-em-uso"))
	        .body("title", is("Entidade em uso"))
	        .body("detail", is("Cozinha de código 2 não pode ser removida, pois está em uso"))
	        .body("userMessage", is("Cozinha de código 2 não pode ser removida, pois está em uso"));			;
	}	
}

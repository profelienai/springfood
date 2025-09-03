package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasItem;
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
@Sql(scripts = "/sql/CadastroCidadeServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CidadeControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonCidadeVR;
	private String jsonCidadeComDadosInvalidos;
	private String jsonCidadeComIdEstadoNulo;
	private String jsonCidadeComEstadoInexistente;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cidades";

		jsonCidadeVR = ResourceUtils.getContentFromResource(
				"/json/correto/cidade-vr.json");
		
		jsonCidadeComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/cidade-com-dados-invalidos.json");

		jsonCidadeComIdEstadoNulo = ResourceUtils.getContentFromResource(
				"/json/incorreto/cidade-com-id-estado-nulo.json");	
		
		jsonCidadeComEstadoInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/cidade-com-estado-inexistente.json");		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarCidades() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("nome", hasItems("Belo Horizonte", "Rio de Janeiro"))
	    	.body("estado.nome", hasItems("Minas Gerais", "Rio de Janeiro"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarCidadeExistente() {
		given()
			.pathParam("cidadeId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Belo Horizonte"))
			.body("estado.id", is(2))
			.body("estado.nome", is("Minas Gerais"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarCidade() {
		given()
			.body(jsonCidadeVR)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", is(3))
			.body("nome", is("Volta Redonda"))
			.body("estado.id", is(1))
			.body("estado.nome", is("Rio de Janeiro"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarCidadeExistente() {
		given()
			.pathParam("cidadeId", 1L)
			.body(jsonCidadeVR)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Volta Redonda"))
			.body("estado.id", is(1))
			.body("estado.nome", is("Rio de Janeiro"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoRemoverCidadeExistente() {
		given()
			.pathParam("cidadeId", 1L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	


	@Test
	public void deveRetornarStatus404_QuandoConsultarCidadeInexistente() {
		given()
			.pathParam("cidadeId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de cidade com código 99"))
	        .body("userMessage", is("Não existe um cadastro de cidade com código 99"));			
	}	

		
	@Test
	public void deveRetornarStatus400_QuandoAdicionarCidadeComDadosInvalidos() {
		given()
			.body(jsonCidadeComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))

			.body("objects", hasSize(2))
			
			.body("objects.name", hasItem("estado"))
			.body("objects.userMessage", hasItem("Estado da cidade é obrigatório"))		
			
			.body("objects.name", hasItem("nome"))
			.body("objects.userMessage", hasItem("Nome da cidade é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarCidadeComEstadoInvalido() {
		given()
			.body(jsonCidadeComIdEstadoNulo)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			
			.body("objects", hasSize(1))
			.body("objects[0].name", is("estado.id"))
			.body("objects[0].userMessage", is("Código do estado é obrigatório"));		
	}
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarCidadeComEstadoInexistente() {
		given()
			.body(jsonCidadeComEstadoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Violação de regra de negócio"))
			.body("detail", is("Não existe um cadastro de estado com código 99"))
			.body("userMessage", is("Não existe um cadastro de estado com código 99"));		
	}			
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarCidadeComDadosInvalidos() {
		given()
			.pathParam("cidadeId", 1L)
			.body(jsonCidadeComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			
			.body("objects", hasSize(2))
			
			.body("objects.name", hasItem("estado"))
			.body("objects.userMessage", hasItem("Estado da cidade é obrigatório"))		
			
			.body("objects.name", hasItem("nome"))
			.body("objects.userMessage", hasItem("Nome da cidade é obrigatório"));			
	}		
	
	@Test
	public void deveRetornarStatus404_QuandoAlterarCidadeInexistente() {
		given()
			.pathParam("cidadeId", 99L)
			.body(jsonCidadeVR)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de cidade com código 99"))
			.body("userMessage", is("Não existe um cadastro de cidade com código 99"));	
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarCidadeComEstadoInexistente() {
		given()
			.pathParam("cidadeId", 1L)
			.body(jsonCidadeComEstadoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Violação de regra de negócio"))
			.body("detail", is("Não existe um cadastro de estado com código 99"))
			.body("userMessage", is("Não existe um cadastro de estado com código 99"));		
	}		

	@Test
	public void deveRetornarStatus404_QuandoRemoverCidadeInexistente() {
		given()
			.pathParam("cidadeId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{cidadeId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de cidade com código 99"))
			.body("userMessage", is("Não existe um cadastro de cidade com código 99"));		
	}
}

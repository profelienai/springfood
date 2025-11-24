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
@Sql(scripts = "/sql/CadastroGrupoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class GrupoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonGrupoSP;
	private String jsonGrupoComDadosInvalidos;
	private String jsonGrupoComAtributoInexistente;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/grupos";

		jsonGrupoSP = ResourceUtils.getContentFromResource(
				"/json/correto/grupo-secretaria.json");
		
		jsonGrupoComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/grupo-com-dados-invalidos.json");
		
		jsonGrupoComAtributoInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/grupo-com-atributo-inexistente.json");		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarGrupos() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("nome", hasItems("Vendedor", "Gerente"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarGrupoExistente() {
		given()
			.pathParam("grupoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{grupoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Gerente"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarGrupo() {
		given()
			.body(jsonGrupoSP)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", is(3))
			.body("nome", is("Secretaria"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarGrupoExistente() {
		given()
			.pathParam("grupoId", 1L)
			.body(jsonGrupoSP)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Secretaria"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoRemoverGrupoExistente() {
		given()
			.pathParam("grupoId", 2L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	

	@Test
	public void deveRetornarStatus404_QuandoConsultarGrupoInexistente() {
		given()
			.pathParam("grupoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de grupo com código 99"))
	        .body("userMessage", is("Não existe um cadastro de grupo com código 99"));			
	}	

	@Test
	public void deveRetornarStatus400_QuandoAdicionarGrupoComDadosInvalidos() {
		given()
			.body(jsonGrupoComDadosInvalidos)
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
			.body("objects[0].userMessage", is("Nome do grupo é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarGrupoComAtributoInexistente() {
		given()
			.body(jsonGrupoComAtributoInexistente)
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
	public void deveRetornarStatus404_QuandoAlterarGrupoInexistente() {
		given()
			.pathParam("grupoId", 99L)
			.body(jsonGrupoSP)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de grupo com código 99"))
			.body("userMessage", is("Não existe um cadastro de grupo com código 99"));	
	}	

	@Test
	public void deveRetornarStatus400_QuandoAlterarGrupoComDadosInvalidos() {
		given()
			.pathParam("grupoId", 99L)
			.body(jsonGrupoComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("nome"))
			.body("objects[0].userMessage", is("Nome do grupo é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarGrupoComAtributoInexistente() {
		given()
			.pathParam("grupoId", 99L)
			.body(jsonGrupoComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{grupoId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'atr_inexistente' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));
	}		
	
	@Test
	public void deveRetornarStatus404_QuandoRemoverGrupoInexistente() {
		given()
			.pathParam("grupoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{grupoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de grupo com código 99"))
			.body("userMessage", is("Não existe um cadastro de grupo com código 99"));		
	}
	
	@Test
	public void deveRetornarStatus409_QuandoRemoverGrupoEmUso() {
		given()
			.pathParam("grupoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{grupoId}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value())
	        .body("status", is(409))
	        .body("type", is("https://springfood.com.br/entidade-em-uso"))
	        .body("title", is("Entidade em uso"))
	        .body("detail", is("Grupo de código 1 não pode ser removido, pois está em uso"))
	        .body("userMessage", is("Grupo de código 1 não pode ser removido, pois está em uso"));			;
	}	
}

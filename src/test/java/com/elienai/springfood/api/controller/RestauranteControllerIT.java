package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
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
@Sql(scripts = "/sql/CadastroRestauranteServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RestauranteControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonRestauranteCadore;
	private String jsonRestauranteComDadosInvalidos;
	private String jsonRestauranteComIdCozinhaNulo;
	private String jsonRestauranteComCozinhaInexistente;
	private String jsonRestauranteComAtributoInexistente;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes";

		jsonRestauranteCadore = ResourceUtils.getContentFromResource(
				"/json/correto/restaurante-cadore.json");
		
		jsonRestauranteComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/restaurante-com-dados-invalidos.json");

		jsonRestauranteComIdCozinhaNulo = ResourceUtils.getContentFromResource(
				"/json/incorreto/restaurante-com-id-cozinha-nulo.json");	
		
		jsonRestauranteComCozinhaInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/restaurante-com-cozinha-inexistente.json");
		
		jsonRestauranteComAtributoInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/restaurante-com-atributo-inexistente.json");		
		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarRestaurantes() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("nome", hasItems("Aramad", "Di Napoli"))
	    	.body("taxaFrete", hasItems(10.5f, 9.5f))
	    	.body("cozinha.nome", hasItems("Arabe", "Italiana"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarRestauranteExistente() {
		given()
			.pathParam("restauranteId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			
			.body("id", is(1))
			.body("nome", is("Aramad"))
			.body("taxaFrete", is(10.5f))
			
			.body("cozinha.id", is(2))
			.body("cozinha.nome", is("Arabe"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarRestaurante() {
		given()
			.body(jsonRestauranteCadore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())

			.body("id", is(3))
			.body("nome", is("Cadore"))
			.body("taxaFrete", is(10.99f))
			
			.body("cozinha.id", is(1))
			.body("cozinha.nome", is("Italiana"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarRestauranteExistente() {
		given()
			.pathParam("restauranteId", 1L)
			.body(jsonRestauranteCadore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.OK.value())

			.body("id", is(1))
			.body("nome", is("Cadore"))
			.body("taxaFrete", is(10.99f))
			
			.body("cozinha.id", is(1))
			.body("cozinha.nome", is("Italiana"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoRemoverRestauranteExistente() {
		given()
			.pathParam("restauranteId", 1L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{restauranteId}")
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
			.get("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de restaurante com código 99"))
	        .body("userMessage", is("Não existe um cadastro de restaurante com código 99"));			
	}	


	@Test
	public void deveRetornarStatus400_QuandoAdicionarRestauranteComDadosInvalidos() {
		given()
			.body(jsonRestauranteComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))

			.body("objects", hasSize(3))
			
			.body("objects.name", hasItem("nome"))
			.body("objects.userMessage", hasItem("Nome do restaurante é obrigatório"))		
			
			.body("objects.name", hasItem("cozinha"))
			.body("objects.userMessage", hasItem("Cozinha do restaurante é obrigatória"))
			
			.body("objects.name", hasItem("taxaFrete"))
			.body("objects.userMessage", hasItem("Taxa de frete do restaurante é obrigatória"))
			;		
			
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarRestauranteComCozinhaInvalida() {
		given()
			.body(jsonRestauranteComIdCozinhaNulo)
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
			.body("objects[0].name", is("cozinha.id"))
			.body("objects[0].userMessage", is("Código da cozinha é obrigatório"));		
	}
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarRestauranteComCozinhaInexistente() {
		given()
			.body(jsonRestauranteComCozinhaInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Violação de regra de negócio"))
			.body("detail", is("Não existe um cadastro de cozinha com código 99"))
			.body("userMessage", is("Não existe um cadastro de cozinha com código 99"));

	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarRestauranteComAtributoInexistente() {
		given()
			.body(jsonRestauranteComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'cozinha.nome' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));		
	}		
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarRestauranteComDadosInvalidos() {
		given()
			.pathParam("restauranteId", 1L)
			.body(jsonRestauranteComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			
			.body("objects", hasSize(3))
			
			.body("objects.name", hasItem("nome"))
			.body("objects.userMessage", hasItem("Nome do restaurante é obrigatório"))		
			
			.body("objects.name", hasItem("cozinha"))
			.body("objects.userMessage", hasItem("Cozinha do restaurante é obrigatória"))
			
			.body("objects.name", hasItem("taxaFrete"))
			.body("objects.userMessage", hasItem("Taxa de frete do restaurante é obrigatória"));
	}		
	
	@Test
	public void deveRetornarStatus404_QuandoAlterarRestauranteInexistente() {
		given()
			.pathParam("restauranteId", 99L)
			.body(jsonRestauranteCadore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de restaurante com código 99"))
			.body("userMessage", is("Não existe um cadastro de restaurante com código 99"));	
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarRestauranteComCozinhaInexistente() {
		given()
			.pathParam("restauranteId", 1L)
			.body(jsonRestauranteComCozinhaInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Violação de regra de negócio"))
			.body("detail", is("Não existe um cadastro de cozinha com código 99"))
			.body("userMessage", is("Não existe um cadastro de cozinha com código 99"));		
	}		
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarRestauranteComAtributoInexistente() {
		given()
			.pathParam("restauranteId", 1L)
			.body(jsonRestauranteComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'cozinha.nome' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));		
	}		

	@Test
	public void deveRetornarStatus404_QuandoRemoverRestauranteInexistente() {
		given()
			.pathParam("restauranteId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{restauranteId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de restaurante com código 99"))
			.body("userMessage", is("Não existe um cadastro de restaurante com código 99"));		
	}
}

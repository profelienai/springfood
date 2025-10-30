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
@Sql(scripts = "/sql/CadastroFormaPagamentoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FormaPagamentoControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonFormaPagamentoDinheiro;
	private String jsonFormaPagamentoComDadosInvalidos;
	private String jsonFormaPagamentoComAtributoInexistente;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/formas-pagamento";

		jsonFormaPagamentoDinheiro = ResourceUtils.getContentFromResource(
				"/json/correto/forma-pagamento-dinheiro.json");
		
		jsonFormaPagamentoComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/forma-pagamento-com-dados-invalidos.json");
		
		jsonFormaPagamentoComAtributoInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/forma-pagamento-com-atributo-inexistente.json");		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarFormasPagamento() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(3))    
	    	.body("descricao", hasItems("Cartao de credito", "Cartao de debito", "Dinheiro"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarFormaPagamentoExistente() {
		given()
			.pathParam("formaPagamentoId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("descricao", is("Cartao de credito"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarFormaPagamento() {
		given()
			.body(jsonFormaPagamentoDinheiro)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", is(4))
			.body("descricao", is("Dinheiro"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarFormaPagamentoExistente() {
		given()
			.pathParam("formaPagamentoId", 1L)
			.body(jsonFormaPagamentoDinheiro)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("descricao", is("Dinheiro"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoRemoverFormaPagamentoExistente() {
		given()
			.pathParam("formaPagamentoId", 3L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.body(is(emptyOrNullString()));
	}	

	@Test
	public void deveRetornarStatus404_QuandoConsultarFormaPagamentoInexistente() {
		given()
			.pathParam("formaPagamentoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de forma de pagamento com código 99"))
	        .body("userMessage", is("Não existe um cadastro de forma de pagamento com código 99"));			
	}	

	@Test
	public void deveRetornarStatus400_QuandoAdicionarFormaPagamentoComDadosInvalidos() {
		given()
			.body(jsonFormaPagamentoComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("descricao"))
			.body("objects[0].userMessage", is("Descrição da forma de pagamento é obrigatória"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarFormaPagamentoComAtributoInexistente() {
		given()
			.body(jsonFormaPagamentoComAtributoInexistente)
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
	public void deveRetornarStatus404_QuandoAlterarFormaPagamentoInexistente() {
		given()
			.pathParam("formaPagamentoId", 99L)
			.body(jsonFormaPagamentoDinheiro)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de forma de pagamento com código 99"))
			.body("userMessage", is("Não existe um cadastro de forma de pagamento com código 99"));	
	}	

	@Test
	public void deveRetornarStatus400_QuandoAlterarFormaPagamentoComDadosInvalidos() {
		given()
			.pathParam("formaPagamentoId", 99L)
			.body(jsonFormaPagamentoComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("descricao"))
			.body("objects[0].userMessage", is("Descrição da forma de pagamento é obrigatória"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarFormaPagamentoComAtributoInexistente() {
		given()
			.pathParam("formaPagamentoId", 99L)
			.body(jsonFormaPagamentoComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'atr_inexistente' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));
	}		
	
	@Test
	public void deveRetornarStatus404_QuandoRemoverFormaPagamentoInexistente() {
		given()
			.pathParam("formaPagamentoId", 99L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de forma de pagamento com código 99"))
			.body("userMessage", is("Não existe um cadastro de forma de pagamento com código 99"));		
	}
	
	@Test
	public void deveRetornarStatus409_QuandoRemoverFormaPagamentoEmUso() {
		given()
			.pathParam("formaPagamentoId", 2L)
			.accept(ContentType.JSON)
		.when()
			.delete("/{formaPagamentoId}")
		.then()
			.statusCode(HttpStatus.CONFLICT.value())
	        .body("status", is(409))
	        .body("type", is("https://springfood.com.br/entidade-em-uso"))
	        .body("title", is("Entidade em uso"))
	        .body("detail", is("Forma de pagamento de código 2 não pode ser removida, pois está em uso"))
	        .body("userMessage", is("Forma de pagamento de código 2 não pode ser removida, pois está em uso"));			;
	}	
}

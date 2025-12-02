package com.elienai.springfood.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
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
@Sql(scripts = "/sql/CadastroUsuarioServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UsuarioControllerIT {

	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	private String jsonUsuario;
	private String jsonUsuarioComSenha;
	private String jsonUsuarioAlteraSenha;
	private String jsonUsuarioComDadosInvalidos;
	private String jsonUsuarioComSenhaComDadosInvalidos;
	private String jsonUsuarioComAtributoInexistente;
	
	@BeforeEach
	private void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/usuarios";

		jsonUsuario = ResourceUtils.getContentFromResource(
				"/json/correto/usuario-joao.json");

		jsonUsuarioComSenha = ResourceUtils.getContentFromResource(
				"/json/correto/usuario-com-senha-joao.json");

		jsonUsuarioAlteraSenha = ResourceUtils.getContentFromResource(
				"/json/correto/usuario-altera-senha.json");
		
		jsonUsuarioComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/usuario-com-dados-invalidos.json");

		jsonUsuarioComSenhaComDadosInvalidos = ResourceUtils.getContentFromResource(
				"/json/incorreto/usuario-com-senha-com-dados-invalidos.json");
		
		jsonUsuarioComAtributoInexistente = ResourceUtils.getContentFromResource(
				"/json/incorreto/usuario-com-atributo-inexistente.json");		
	}
	
	@AfterEach
	private void cleanDatabase() {
		databaseCleaner.clearTables();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarUsuarios() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value())
	    	.body("", hasSize(2))    
	    	.body("nome", hasItems("João da Silva", "Maria Joaquina"))
			.body("email", hasItems("joao@gmail.com", "maria@gmail.com"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarUsuarioExistente() {
		given()
			.pathParam("usuarioId", 1L)
			.accept(ContentType.JSON)
		.when()
			.get("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("João da Silva"))
			.body("email", is("joao@gmail.com"));
	}	
	
	@Test
	public void deveRetornarStatus201_QuandoAdicionarUsuario() {
		given()
			.body(jsonUsuarioComSenha)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.body("id", is(3))
			.body("nome", is("Joao da Silva"))
			.body("email", is("joaosilva@gmail.com"));
	}
	
	@Test
	public void deveRetornarStatus200_QuandoAlterarUsuarioExistente() {
		given()
			.pathParam("usuarioId", 1L)
			.body(jsonUsuario)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", is(1))
			.body("nome", is("Joao da Silva"))
			.body("email", is("joaosilva@gmail.com"));
	}	

	@Test
	public void deveRetornarStatus204_QuandoAlterarSenha() {
		given()
			.pathParam("usuarioId", 1L)
			.body(jsonUsuarioAlteraSenha)
			.contentType(ContentType.JSON)
		.when()
			.put("/{usuarioId}/senha")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}	
	
	@Test
	public void deveRetornarStatus404_QuandoConsultarUsuarioInexistente() {
		given()
			.pathParam("usuarioId", 99L)
			.accept(ContentType.JSON)
		.when()
			.get("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
	        .body("status", is(404))
	        .body("title", is("Recurso não encontrado"))
	        .body("detail", is("Não existe um cadastro de usuário com código 99"))
	        .body("userMessage", is("Não existe um cadastro de usuário com código 99"));			
	}	

	@Test
	public void deveRetornarStatus400_QuandoAdicionarUsuarioComDadosInvalidos() {
		given()
			.body(jsonUsuarioComSenhaComDadosInvalidos)
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
			.body("objects[0].userMessage", is("Nome do usuário é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAdicionarUsuarioComAtributoInexistente() {
		given()
			.body(jsonUsuarioComAtributoInexistente)
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
	public void deveRetornarStatus404_QuandoAlterarUsuarioInexistente() {
		given()
			.pathParam("usuarioId", 99L)
			.body(jsonUsuario)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("status", is(404))
			.body("type",  is("https://springfood.com.br/recurso-nao-encontrado"))
			.body("title", is("Recurso não encontrado"))
			.body("detail", is("Não existe um cadastro de usuário com código 99"))
			.body("userMessage", is("Não existe um cadastro de usuário com código 99"));	
	}	

	@Test
	public void deveRetornarStatus400_QuandoAlterarUsuarioComDadosInvalidos() {
		given()
			.pathParam("usuarioId", 99L)
			.body(jsonUsuarioComDadosInvalidos)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Dados inválidos"))
			.body("detail", containsString("Um ou mais campos estão inválidos"))
			.body("objects[0].name", is("nome"))
			.body("objects[0].userMessage", is("Nome do usuário é obrigatório"));		
	}	
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarUsuarioComAtributoInexistente() {
		given()
			.pathParam("usuarioId", 99L)
			.body(jsonUsuarioComAtributoInexistente)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Mensagem incompreensível"))
			.body("detail", is("A propriedade 'atr_inexistente' não existe. Corrija ou remova essa propriedade e tente novamente."))
			.body("userMessage", is("Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema."));
	}
	
	@Test
	public void deveRetornarStatus400_QuandoAlterarSenhaQueNaoCoincide() {
		given()
			.pathParam("usuarioId", 2L)
			.body(jsonUsuarioAlteraSenha)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/{usuarioId}/senha")
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("status", is(400))
			.body("title", is("Violação de regra de negócio"))
			.body("detail", is("Senha atual informada não coincide com a senha do usuário."))
			.body("userMessage", is("Senha atual informada não coincide com a senha do usuário."));
					
	}		
}

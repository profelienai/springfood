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
@Sql(scripts = "/sql/CadastroProdutoServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RestauranteProdutoControllerIT {

    @LocalServerPort
    private int port;
    
    @Autowired
    private DatabaseCleaner databaseCleaner;

    private String jsonProdutoBatata;
    private String jsonProdutoComDadosInvalidos;
    private String jsonProdutoComAtributoInexistente;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/restaurantes/{restauranteId}/produtos";

        jsonProdutoBatata = ResourceUtils.getContentFromResource(
                "/json/correto/produto-batata.json");

        jsonProdutoComDadosInvalidos = ResourceUtils.getContentFromResource(
                "/json/incorreto/produto-com-dados-invalidos.json");

        jsonProdutoComAtributoInexistente = ResourceUtils.getContentFromResource(
                "/json/incorreto/produto-com-atributo-inexistente.json");
    }

    @AfterEach
    void clean() {
        databaseCleaner.clearTables();
    }

    // -------------------------------------------------------------
    // LISTAGEM
    // -------------------------------------------------------------

    @Test
    void deveRetornarStatus200_QuandoConsultarProdutos() {
        given()
            .pathParam("restauranteId", 1L)
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("", hasSize(2))

            .body("nome", hasItems("Porco com molho agridoce", "Camarão tailandês"))
            .body("descricao", hasItems(
                    "Deliciosa carne suína ao molho especial",
                    "16 camarões grandes ao molho picante"))
            .body("preco", hasItems(78.90f, 110f))
            .body("ativo", hasItems(true, true));
    }

    // -------------------------------------------------------------
    // CONSULTA POR ID
    // -------------------------------------------------------------

    @Test
    void deveRetornarStatus200_QuandoConsultarProdutoExistente() {
        given()
            .pathParam("restauranteId", 1L)
            .pathParam("produtoId", 1L)
            .accept(ContentType.JSON)
        .when()
            .get("/{produtoId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(1))
            .body("nome", is("Porco com molho agridoce"))
            .body("descricao", is("Deliciosa carne suína ao molho especial"))
            .body("preco", is(78.90f))
            .body("ativo", is(true));
    }

    @Test
    void deveRetornarStatus404_QuandoConsultarProdutoInexistente() {
        given()
            .pathParam("restauranteId", 1L)
            .pathParam("produtoId", 999L)
            .accept(ContentType.JSON)
        .when()
            .get("/{produtoId}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("status", is(404))
            .body("title", is("Recurso não encontrado"))
            .body("detail", is("Não existe um cadastro de produto com código 999 para o restaurante de código 1"))
            .body("userMessage", is("Não existe um cadastro de produto com código 999 para o restaurante de código 1"));
    }

    // -------------------------------------------------------------
    // CADASTRO
    // -------------------------------------------------------------

    @Test
    void deveRetornarStatus201_QuandoCadastrarProduto() {
        given()
            .pathParam("restauranteId", 1L)
            .body(jsonProdutoBatata)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())

            .body("id", is(3))
            .body("nome", is("Batata Frita"))
            .body("descricao", is("Porção média"))
            .body("preco", is(12.50f))
            .body("ativo", is(true));
    }

    @Test
    void deveRetornarStatus400_QuandoCadastrarProdutoComDadosInvalidos() {
        given()
            .pathParam("restauranteId", 1L)
            .body(jsonProdutoComDadosInvalidos)
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
            .body("objects.name", hasItems("nome", "preco", "descricao"));
    }

    @Test
    void deveRetornarStatus400_QuandoCadastrarProdutoComAtributoInexistente() {
        given()
            .pathParam("restauranteId", 1L)
            .body(jsonProdutoComAtributoInexistente)
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

    // -------------------------------------------------------------
    // ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    void deveRetornarStatus200_QuandoAlterarProduto() {
        given()
            .pathParam("restauranteId", 1L)
            .pathParam("produtoId", 1L)
            .body(jsonProdutoBatata)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .put("/{produtoId}")
        .then()
            .statusCode(HttpStatus.OK.value())

            .body("id", is(1))
            .body("nome", is("Batata Frita"))
            .body("descricao", is("Porção média"))
            .body("preco", is(12.50f))
            .body("ativo", is(true));
    }

    @Test
    void deveRetornarStatus404_QuandoAlterarProdutoInexistente() {
        given()
            .pathParam("restauranteId", 1L)
            .pathParam("produtoId", 999L)
            .body(jsonProdutoBatata)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .put("/{produtoId}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("detail", is("Não existe um cadastro de produto com código 999 para o restaurante de código 1"));
    }
}

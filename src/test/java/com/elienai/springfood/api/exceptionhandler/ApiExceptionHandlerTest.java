package com.elienai.springfood.api.exceptionhandler;

import static com.elienai.springfood.api.exceptionhandler.ProblemType.ENTIDADE_EM_USO;
import static com.elienai.springfood.api.exceptionhandler.ProblemType.ERRO_DE_SISTEMA;
import static com.elienai.springfood.api.exceptionhandler.ProblemType.ERRO_NEGOCIO;
import static com.elienai.springfood.api.exceptionhandler.ProblemType.MENSAGEM_INCOMPREENSIVEL;
import static com.elienai.springfood.api.exceptionhandler.ProblemType.PARAMETRO_INVALIDO;
import static com.elienai.springfood.api.exceptionhandler.ProblemType.RECURSO_NAO_ENCONTRADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.elienai.springfood.domain.exception.EntidadeEmUsoException;
import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.exception.NegocioException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class ApiExceptionHandlerTest {

	public static final String MSG_ERRO_GENERICA_USUARIO_FINAL
		= "Ocorreu um erro interno inesperado no sistema. Tente novamente e se "
				+ "o problema persistir, entre em contato com o administrador do sistema.";
	
	private ApiExceptionHandler handler;
	private WebRequest request;
	
	@BeforeEach
	void setUp() {
		handler = new ApiExceptionHandler();
		request = mock(WebRequest.class);
	}
	
	@Test  @SuppressWarnings("serial")
	void deveTratarEntidadeNaoEncontradaException() {
		EntidadeNaoEncontradaException ex = new EntidadeNaoEncontradaException("Entidade não encontrada") {};
		
		ResponseEntity<?> response = handler.handleEntidadeNaoEncontrada(ex, request);
		Problem problem = (Problem) response.getBody();
		
		assertEquals(NOT_FOUND, response.getStatusCode());
		assertEquals(NOT_FOUND.value(), problem.getStatus());
		assertEquals(RECURSO_NAO_ENCONTRADO.getUri(), problem.getType());
		assertEquals(RECURSO_NAO_ENCONTRADO.getTitle(), problem.getTitle());
		assertEquals("Entidade não encontrada", problem.getDetail());
		assertEquals("Entidade não encontrada", problem.getUserMessage());
	}
	
	@Test @SuppressWarnings("serial")
	void deveTratarEntidadeEmUsoException() {
		EntidadeEmUsoException ex = new EntidadeEmUsoException("Entidade em uso") {};
		
		ResponseEntity<?> response = handler.handleEntidadeEmUso(ex, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(CONFLICT, response.getStatusCode());
		assertEquals(CONFLICT.value(), problem.getStatus());
		assertEquals(ENTIDADE_EM_USO.getUri(), problem.getType());
		assertEquals(ENTIDADE_EM_USO.getTitle(), problem.getTitle());
		assertEquals("Entidade em uso", problem.getDetail());
		assertEquals("Entidade em uso", problem.getUserMessage());
	}
	
	@Test
	void deveTratarNegocioException() {
		NegocioException ex = new NegocioException("Erro de negócio");
		
		ResponseEntity<?> response = handler.handleNegocio(ex, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(BAD_REQUEST, response.getStatusCode());
		assertEquals(BAD_REQUEST.value(), problem.getStatus());
		assertEquals(ERRO_NEGOCIO.getUri(), problem.getType());
		assertEquals(ERRO_NEGOCIO.getTitle(), problem.getTitle());
		assertEquals("Erro de negócio", problem.getDetail());
		assertEquals("Erro de negócio", problem.getUserMessage());
	}	
	
	@Test
	void deveTratarNoHandlerFoundException() {
		NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/nao-existe", new HttpHeaders());
		
		ResponseEntity<?> response = handler.handleNoHandlerFoundException(ex, new HttpHeaders(), NOT_FOUND, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(NOT_FOUND, response.getStatusCode());
		assertEquals(NOT_FOUND.value(), problem.getStatus());
		assertEquals(RECURSO_NAO_ENCONTRADO.getUri(), problem.getType());
		assertEquals(RECURSO_NAO_ENCONTRADO.getTitle(), problem.getTitle());
		assertEquals("O recurso /nao-existe, que você tentou acessar, é inexistente.", problem.getDetail());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getUserMessage());
	}		
	
	@Test
	void deveTratarExceptionGenerica() {
		Exception ex = new Exception("Erro genérico");
		
		ResponseEntity<?> response = handler.handleUncaught(ex, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals(INTERNAL_SERVER_ERROR.value(), problem.getStatus());
		assertEquals(ERRO_DE_SISTEMA.getUri(), problem.getType());
		assertEquals(ERRO_DE_SISTEMA.getTitle(), problem.getTitle());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getDetail());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getUserMessage());
	}	
	
	@Test
	void deveTratarTypeMismatchException() {
		TypeMismatchException ex = new MethodArgumentTypeMismatchException("abc", Integer.class, "idade", null, new IllegalArgumentException("invalido"));
		
		ResponseEntity<?> response = handler.handleTypeMismatch(ex, new HttpHeaders(), BAD_REQUEST, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(BAD_REQUEST, response.getStatusCode());
		assertEquals(BAD_REQUEST.value(), problem.getStatus());
		assertEquals(PARAMETRO_INVALIDO.getUri(), problem.getType());
		assertEquals(PARAMETRO_INVALIDO.getTitle(), problem.getTitle());
		assertEquals("O parâmetro de URL 'idade' recebeu o valor 'abc', que é de um tipo inválido. Corrija e informe um valor compatível com o tipo Integer.", problem.getDetail());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getUserMessage());
	}
	
	@Test
	void deveTratarHttpMessageNotReadableException() {
		Throwable genericaCause = new RuntimeException("Erro inesperado");
		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("msg", genericaCause, null);
		
		ResponseEntity<?> response = handler.handleHttpMessageNotReadable(ex, new HttpHeaders(), BAD_REQUEST, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(BAD_REQUEST, response.getStatusCode());
		assertEquals(BAD_REQUEST.value(), problem.getStatus());
		assertEquals(MENSAGEM_INCOMPREENSIVEL.getUri(), problem.getType());
		assertEquals(MENSAGEM_INCOMPREENSIVEL.getTitle(), problem.getTitle());
		assertEquals("O corpo da requisição é inválido. Verifique erro de sintaxe.", problem.getDetail());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getUserMessage());
	}	
	
	@Test
	void deveTratarInvalidFormatException() {
		InvalidFormatException cause = new InvalidFormatException(null, "msg", "abc", Integer.class);
		cause.prependPath(Object.class, "idade");
		
		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("msg", cause, null);
		
		ResponseEntity<?> response = handler.handleHttpMessageNotReadable(ex, new HttpHeaders(), BAD_REQUEST, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(BAD_REQUEST, response.getStatusCode());
		assertEquals(BAD_REQUEST.value(), problem.getStatus());
		assertEquals(MENSAGEM_INCOMPREENSIVEL.getUri(), problem.getType());
		assertEquals(MENSAGEM_INCOMPREENSIVEL.getTitle(), problem.getTitle());
		assertEquals("A propriedade 'idade' recebeu o valor 'abc', que é de um tipo inválido. Corrija e informe um valor compatível com o tipo Integer.", problem.getDetail());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getUserMessage());
	}	
	
	@Test
	void deveTratarPropertyBindingException() throws JsonParseException, IOException {
		JsonParser parser = new JsonFactory().createParser("");
		
		PropertyBindingException  cause = UnrecognizedPropertyException.from(parser, String.class, "nome", null);
		
		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("msg", cause, null);
		
		ResponseEntity<?> response = handler.handleHttpMessageNotReadable(ex, new HttpHeaders(), BAD_REQUEST, request);
		Problem problem = (Problem) response.getBody();

		assertEquals(BAD_REQUEST, response.getStatusCode());
		assertEquals(BAD_REQUEST.value(), problem.getStatus());
		assertEquals(MENSAGEM_INCOMPREENSIVEL.getUri(), problem.getType());
		assertEquals(MENSAGEM_INCOMPREENSIVEL.getTitle(), problem.getTitle());
		assertEquals("A propriedade 'nome' não existe. Corrija ou remova essa propriedade e tente novamente.", problem.getDetail());
		assertEquals(MSG_ERRO_GENERICA_USUARIO_FINAL, problem.getUserMessage());
	}	
}

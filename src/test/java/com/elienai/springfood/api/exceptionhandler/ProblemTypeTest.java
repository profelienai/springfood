package com.elienai.springfood.api.exceptionhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ProblemTypeTest {

	static Stream<Arguments> provideProblemTypes() {
	    return Stream.of(
	        Arguments.of("DADOS_INVALIDOS", "/dados-invalidos", "Dados inválidos"),
	        Arguments.of("RECURSO_NAO_ENCONTRADO", "/recurso-nao-encontrado", "Recurso não encontrado"),
	        Arguments.of("ENTIDADE_EM_USO", "/entidade-em-uso", "Entidade em uso"),
	        Arguments.of("ERRO_NEGOCIO", "/erro-negocio", "Violação de regra de negócio"),
	        Arguments.of("MENSAGEM_INCOMPREENSIVEL", "/mensagem-incompreensivel", "Mensagem incompreensível"),
	        Arguments.of("PARAMETRO_INVALIDO", "/parametro-invalido", "Parâmetro inválido"),
	        Arguments.of("ERRO_DE_SISTEMA", "/erro-de-sistema", "Erro de sistema")
	    );
	}
	
	@Test
	public void deveConterTodosProblemTypes() {
		ProblemType[] values = ProblemType.values();
		assertEquals(provideProblemTypes().count(), values.length);
	}
	
    @ParameterizedTest(name = "{0} deve ter uri e título corretos")
    @MethodSource("provideProblemTypes")
    void deveGerarUriTitleCorretos(String problemTypeName, String path, String expectedTitle) {
        ProblemType problem = ProblemType.valueOf(problemTypeName);

        assertEquals("https://springfood.com.br" + path, problem.getUri());
        assertEquals(expectedTitle, problem.getTitle());
    }	
}

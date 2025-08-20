package com.elienai.springfood.api.exceptionhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ProblemTypeTest {

	@Test
	public void deveConterTodosProblemTypes() {
		ProblemType[] values = ProblemType.values();
		assertEquals(6, values.length);
	}
	
    @ParameterizedTest(name = "{0} deve ter uri e título corretos")
    @CsvSource({
        "RECURSO_NAO_ENCONTRADO, /recurso-nao-encontrado, Recurso não encontrado",
        "ENTIDADE_EM_USO, /entidade-em-uso, Entidade em uso",
        "ERRO_NEGOCIO, /erro-negocio, Violação de regra de negócio",
        "MENSAGEM_INCOMPREENSIVEL, /mensagem-incompreensivel, Mensagem incompreensível",
        "PARAMETRO_INVALIDO, /parametro-invalido, Parâmetro inválido",
        "ERRO_DE_SISTEMA, /erro-de-sistema, Erro de sistema"
    })
    void deveGerarUriTitleCorretos(String problemTypeName, String path, String expectedTitle) {
        ProblemType problem = ProblemType.valueOf(problemTypeName);

        assertEquals("https://springfood.com.br" + path, problem.getUri());
        assertEquals(expectedTitle, problem.getTitle());
    }	
}

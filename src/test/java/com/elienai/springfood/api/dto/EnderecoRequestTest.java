package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnderecoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveSerValido_quandoTodosCamposPreenchidos() {
        var cidade = new CidadeIdRequest();
        cidade.setId(1L);

        var endereco = new EnderecoRequest();
        endereco.setCep("38400-999");
        endereco.setLogradouro("Rua João Pinheiro");
        endereco.setNumero("1000");
        endereco.setComplemento("C1");
        endereco.setBairro("Centro");
        endereco.setCidade(cidade);

        var violations = validator.validate(endereco);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalhar_QuandoCamposObrigatoriosNaoPreenchidos() {
        var endereco = new EnderecoRequest();

        var violations = validator.validate(endereco);
	 	
	 	assertThat(violations)
	 		.hasSize(5)
	 	    .extracting(v -> v.getPropertyPath().toString())
	 	    .contains("cep", "logradouro", "numero", "bairro", "cidade");
	 		
	 	assertThat(violations)	 	    
	 	    .extracting(ConstraintViolation::getMessage)
	 		.contains("não deve estar em branco", "não deve ser nulo");
    }

    @Test
    void devePermitirComplementoNulo() {
        var cidade = new CidadeIdRequest();
        cidade.setId(10L);

        var endereco = new EnderecoRequest();
        endereco.setCep("38400-000");
        endereco.setLogradouro("Rua A");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade(cidade);
        endereco.setComplemento(null);

        var violations = validator.validate(endereco);

        assertThat(violations).isEmpty();
        assertThat(endereco.getComplemento()).isNull();
    }
}

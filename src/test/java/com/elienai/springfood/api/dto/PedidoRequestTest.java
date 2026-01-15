package com.elienai.springfood.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PedidoRequestTest {

    private static Validator validator;

    private static RestauranteIdRequest restaurante;
    private static EnderecoRequest endereco;
    private static FormaPagamentoIdRequest formaPagamento;
    private static ItemPedidoRequest item;

    @BeforeAll
    static void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        restaurante = new RestauranteIdRequest();
        restaurante.setId(1L);

        var cidade = new CidadeIdRequest();
        cidade.setId(1L);

        endereco = new EnderecoRequest();
        endereco.setCep("38400-999");
        endereco.setLogradouro("Rua João Pinheiro");
        endereco.setNumero("1000");
        endereco.setBairro("Centro");
        endereco.setCidade(cidade);

        formaPagamento = new FormaPagamentoIdRequest();
        formaPagamento.setId(1L);

        item = new ItemPedidoRequest();
        item.setProdutoId(1L);
        item.setQuantidade(1);
    }

    @Test
    void deveSerValido_quandoTodosCamposPreenchidos() {
        var request = new PedidoRequest();
        request.setRestaurante(restaurante);
        request.setEnderecoEntrega(endereco);
        request.setFormaPagamento(formaPagamento);
        request.setItens(List.of(item));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalhar_quandoCamposObrigatoriosNulos() {
        var request = new PedidoRequest();

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(4)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("restaurante", "enderecoEntrega", "formaPagamento", "itens");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("não deve ser nulo");
    }

    @Test
    void deveFalhar_quandoListaDeItensVazia() {
        var request = new PedidoRequest();
        request.setRestaurante(restaurante);
        request.setEnderecoEntrega(endereco);
        request.setFormaPagamento(formaPagamento);
        request.setItens(List.of());

        var violations = validator.validate(request);

        assertThat(violations)
            .hasSize(1)
            .extracting(v -> v.getPropertyPath().toString())
            .contains("itens");

        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("tamanho deve ser entre 1 e 2147483647");
    }
}

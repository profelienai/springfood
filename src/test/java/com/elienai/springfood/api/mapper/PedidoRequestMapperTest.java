package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CidadeIdRequest;
import com.elienai.springfood.api.dto.EnderecoRequest;
import com.elienai.springfood.api.dto.FormaPagamentoIdRequest;
import com.elienai.springfood.api.dto.ItemPedidoRequest;
import com.elienai.springfood.api.dto.PedidoRequest;
import com.elienai.springfood.api.dto.RestauranteIdRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.model.ItemPedido;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.Restaurante;

class PedidoRequestMapperTest {

    private PedidoRequestMapper mapper;
    private ModelMapper modelMapper;

    private PedidoRequest pedidoRequest;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapperConfig().modelMapper();
        mapper = new PedidoRequestMapper(modelMapper);

        var restaurante = new RestauranteIdRequest();
        restaurante.setId(1L);

        var cidade = new CidadeIdRequest();
        cidade.setId(10L);

        var enderecoEntrega = new EnderecoRequest();
        enderecoEntrega.setCep("38400-999");
        enderecoEntrega.setLogradouro("Rua João Pinheiro");
        enderecoEntrega.setNumero("1000");
        enderecoEntrega.setComplemento("Apto 101");
        enderecoEntrega.setBairro("Centro");
        enderecoEntrega.setCidade(cidade);

        var formaPagamento = new FormaPagamentoIdRequest();
        formaPagamento.setId(3L);

        var item1 = new ItemPedidoRequest();
        item1.setProdutoId(100L);
        item1.setQuantidade(2);
        item1.setObservacao("Sem cebola");

        var item2 = new ItemPedidoRequest();
        item2.setProdutoId(200L);
        item2.setQuantidade(1);
        item2.setObservacao("Bem passado");

        pedidoRequest = new PedidoRequest();
        pedidoRequest.setRestaurante(restaurante);
        pedidoRequest.setEnderecoEntrega(enderecoEntrega);
        pedidoRequest.setFormaPagamento(formaPagamento);
        pedidoRequest.setItens(List.of(item1, item2));
    }

    @Test
    void deveConverterPedidoRequestParaDomainObject() {
        var pedido = mapper.toDomainObject(pedidoRequest);

        assertThat(pedido).isNotNull();

        assertThat(pedido.getRestaurante())
            .isNotNull()
            .extracting(Restaurante::getId)
            .isEqualTo(1L);

        assertThat(pedido.getFormaPagamento())
            .isNotNull()
            .extracting(FormaPagamento::getId)
            .isEqualTo(3L);

        assertThat(pedido.getEnderecoEntrega())
            .isNotNull()
            .extracting(
                Endereco::getCep,
                Endereco::getLogradouro,
                Endereco::getNumero,
                Endereco::getComplemento,
                Endereco::getBairro
            )
            .containsExactly(
                "38400-999",
                "Rua João Pinheiro",
                "1000",
                "Apto 101",
                "Centro"
            );

        assertThat(pedido.getEnderecoEntrega().getCidade())
            .isNotNull()
            .extracting(Cidade::getId)
            .isEqualTo(10L);

        assertThat(pedido.getItens())
            .hasSize(2);

        assertThat(pedido.getItens())
            .extracting(
            	item -> item.getProduto().getId(),
                ItemPedido::getQuantidade,
                ItemPedido::getObservacao
            )
            .containsExactly(
                tuple(100L, 2, "Sem cebola"),
                tuple(200L, 1, "Bem passado")
            );
    }

    @Test
    void deveCopiarPropriedadesDeRequestParaPedidoExistente() {
        var pedido = new Pedido();
        pedido.setId(99L);

        mapper.copyToDomainObject(pedidoRequest, pedido);

        assertThat(pedido)
            .isNotNull()
            .extracting(Pedido::getId)
            .isEqualTo(99L);

        assertThat(pedido.getRestaurante())
            .isNotNull()
            .extracting(Restaurante::getId)
            .isEqualTo(1L);

        assertThat(pedido.getFormaPagamento())
            .isNotNull()
            .extracting(FormaPagamento::getId)
            .isEqualTo(3L);

        assertThat(pedido.getEnderecoEntrega())
            .isNotNull()
            .extracting(Endereco::getCep)
            .isEqualTo("38400-999");

        assertThat(pedido.getItens())
            .hasSize(2);
    }
}

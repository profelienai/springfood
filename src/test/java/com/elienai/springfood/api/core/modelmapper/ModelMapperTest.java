package com.elienai.springfood.api.core.modelmapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.elienai.springfood.api.dto.EnderecoResponse;
import com.elienai.springfood.api.dto.ItemPedidoRequest;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.model.ItemPedido;

@SpringBootTest
class ModelMapperTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void deveIgnorarIdAoMapearItemPedidoRequestParaItemPedido() {
        ItemPedidoRequest request = new ItemPedidoRequest();
        request.setProdutoId(10L);
        request.setQuantidade(2);

        ItemPedido item = modelMapper.map(request, ItemPedido.class);

        assertThat(item.getId()).isNull();
        assertThat(item.getProduto().getId()).isEqualTo(10);
        assertThat(item.getQuantidade()).isEqualTo(2);
    }

    @Test
    void deveMapearCidadeEEstadoAoConverterEnderecoParaEnderecoResponse() {
        Estado estado = new Estado();
        estado.setNome("São Paulo");

        Cidade cidade = new Cidade();
        cidade.setNome("Campinas");
        cidade.setEstado(estado);

        Endereco endereco = new Endereco();
        endereco.setCidade(cidade);

        EnderecoResponse response = modelMapper.map(endereco, EnderecoResponse.class);

        assertThat(response.getCidade()).isEqualTo("Campinas");
        assertThat(response.getEstado()).isEqualTo("São Paulo");
    }
}

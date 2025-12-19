package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.ProdutoRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Produto;

public class ProdutoRequestMapperTest {

	private ProdutoRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new ProdutoRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterProdutoRequestParaDominObject() {
		var produtoRequest = new ProdutoRequest();
		produtoRequest.setNome("Pizza");
		produtoRequest.setDescricao("Pizza de calabresa");
		produtoRequest.setPreco(new BigDecimal("39.90"));
		produtoRequest.setAtivo(true);		
		
		var produto = mapper.toDomainObject(produtoRequest);
		
        assertThat(produto)
        	.isNotNull()
        	.extracting(Produto::getNome, Produto::getDescricao, Produto::getPreco, Produto::getAtivo)
        	.containsExactly("Pizza", "Pizza de calabresa", new BigDecimal("39.90"), true);
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var produtoRequest = new ProdutoRequest();
		produtoRequest.setNome("Pizza");
		produtoRequest.setDescricao("Pizza de calabresa");
		produtoRequest.setPreco(new BigDecimal("39.90"));
		produtoRequest.setAtivo(true);		
		
		var produto = new Produto();
		produto.setId(1L);
		produto.setNome("Hamburguer");
		produto.setDescricao("X-Tudo");
		produto.setPreco(new BigDecimal("19.90"));
		produto.setAtivo(true);		
		
		mapper.copyToDomainObject(produtoRequest, produto);
		
        assertThat(produto)
        	.isNotNull()
        	.extracting(Produto::getId, Produto::getNome, Produto::getDescricao, Produto::getPreco, Produto::getAtivo)
        	.containsExactly(1L, "Pizza", "Pizza de calabresa", new BigDecimal("39.90"), true);
	}
}

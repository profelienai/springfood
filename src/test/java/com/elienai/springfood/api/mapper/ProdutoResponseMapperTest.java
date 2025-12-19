package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.ProdutoResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Produto;

public class ProdutoResponseMapperTest {

	private ProdutoResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new ProdutoResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterProdutoParaProdutoResponse() {
		
		var produto = new Produto();
		produto.setId(1L);
		produto.setNome("Pizza");
		produto.setDescricao("Pizza de calabresa");
		produto.setPreco(new BigDecimal("39.90"));
		produto.setAtivo(true);	
		
		var produtoReponse = mapper.toResponse(produto);
		
		assertThat(produtoReponse)
			.isNotNull()
			.extracting(ProdutoResponse::getId, ProdutoResponse::getNome, ProdutoResponse::getDescricao, 
					    ProdutoResponse::getPreco, ProdutoResponse::getAtivo)
			.containsExactly(1L, "Pizza", "Pizza de calabresa", new BigDecimal("39.90"), true);
	}
	
	@Test
	void deveConverterCollectionDeProdutoParaCollectionDeProdutoResponse() {
		var produto1 = new Produto();
		produto1.setId(1L);
		produto1.setNome("Pizza");
		produto1.setDescricao("Pizza de calabresa");
		produto1.setPreco(new BigDecimal("39.90"));
		produto1.setAtivo(true);	
		
		var produto2 = new Produto();
		produto2.setId(2L);
		produto2.setNome("Hamburguer");
		produto2.setDescricao("X-Tudo");
		produto2.setPreco(new BigDecimal("19.90"));
		produto2.setAtivo(false);	
		
		var produtos = List.of(produto1, produto2);
		
		var produtosResponse = mapper.toCollectionResponse(produtos);
		
		assertThat(produtosResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(ProdutoResponse::getId, ProdutoResponse::getNome, ProdutoResponse::getDescricao, 
					    ProdutoResponse::getPreco, ProdutoResponse::getAtivo)
			.containsExactlyInAnyOrder(
				tuple(1L, "Pizza", "Pizza de calabresa", new BigDecimal("39.90"), true),
				tuple(2L, "Hamburguer", "X-Tudo", new BigDecimal("19.90"), false));
	}
}

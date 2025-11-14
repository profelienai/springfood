package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CidadeIdRequest;
import com.elienai.springfood.api.dto.CozinhaIdRequest;
import com.elienai.springfood.api.dto.EnderecoRequest;
import com.elienai.springfood.api.dto.RestauranteRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.Restaurante;

public class RestauranteRequestMapperTest {
	private RestauranteRequestMapper mapper;
	private ModelMapper modelMapper;
	
	private RestauranteRequest restauranteRequest;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new RestauranteRequestMapper(modelMapper);
		
        var cidade = new CidadeIdRequest();
        cidade.setId(1L);

        var enderecoRequest = new EnderecoRequest();
        enderecoRequest.setCep("38400-999");
        enderecoRequest.setLogradouro("Rua João Pinheiro");
        enderecoRequest.setNumero("1000");
        enderecoRequest.setComplemento("C1");
        enderecoRequest.setBairro("Centro");
        enderecoRequest.setCidade(cidade);		
        
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(10L);
		
		restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		restauranteRequest.setEndereco(enderecoRequest);
	}
	
	@Test
	void deveConverterRestauranteRequestParaDominObject() {
		var restaurante = mapper.toDomainObject(restauranteRequest);
		
        assertThat(restaurante)
        	.isNotNull()
        	.extracting(Restaurante::getId, Restaurante::getNome, Restaurante::getTaxaFrete)
        	.containsExactly(null, "Di Napoli", new BigDecimal(10.99d));
        
        assertThat(restaurante.getCozinha())
	    	.isNotNull()
	    	.extracting(Cozinha::getId, Cozinha::getNome)
	    	.containsExactly(10L, null);       
        
        assertThat(restaurante.getEndereco())
	    	.isNotNull()
	    	.extracting(Endereco::getCep, Endereco::getLogradouro, Endereco::getNumero, 
	    			    Endereco::getComplemento, Endereco::getBairro)
	    	.containsExactly("38400-999", "Rua João Pinheiro", "1000", "C1", "Centro");
        
        assertThat(restaurante.getEndereco().getCidade())
	    	.isNotNull()
	    	.extracting(Cidade::getId)
	    	.isEqualTo(1L);
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var restaurante = new Restaurante();
		restaurante.setId(1L);
		
		mapper.copyToDomainObject(restauranteRequest, restaurante);
		
        assertThat(restaurante)
        	.isNotNull()
        	.extracting(Restaurante::getId, Restaurante::getNome, Restaurante::getTaxaFrete)
        	.containsExactly(1L, "Di Napoli", new BigDecimal(10.99d));
        
        assertThat(restaurante.getCozinha())
	    	.isNotNull()
	    	.extracting(Cozinha::getId, Cozinha::getNome)
	    	.containsExactly(10L, null); 
        
        assertThat(restaurante.getEndereco())
	    	.isNotNull()
	    	.extracting(Endereco::getCep, Endereco::getLogradouro, Endereco::getNumero, 
	    			    Endereco::getComplemento, Endereco::getBairro)
	    	.containsExactly("38400-999", "Rua João Pinheiro", "1000", "C1", "Centro");
        
        assertThat(restaurante.getEndereco().getCidade())
	    	.isNotNull()
	    	.extracting(Cidade::getId)
	    	.isEqualTo(1L);        
        
	}
}

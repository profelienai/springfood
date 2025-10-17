package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CozinhaIdRequest;
import com.elienai.springfood.api.dto.RestauranteRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;

public class RestauranteRequestMapperTest {
	private RestauranteRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new RestauranteRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterRestauranteRequestParaDominObject() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(10L);

		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
		var restaurante = mapper.toDomainObject(restauranteRequest);
		
        assertThat(restaurante)
        	.isNotNull()
        	.extracting(Restaurante::getId, Restaurante::getNome, Restaurante::getTaxaFrete)
        	.containsExactly(null, "Di Napoli", new BigDecimal(10.99d));
        
        assertThat(restaurante.getCozinha())
	    	.isNotNull()
	    	.extracting(Cozinha::getId, Cozinha::getNome)
	    	.containsExactly(10L, null);        
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var cozinhaIdRequest = new CozinhaIdRequest();
		cozinhaIdRequest.setId(20L);
		
		var restauranteRequest = new RestauranteRequest();
		restauranteRequest.setNome("Di Napoli");
		restauranteRequest.setTaxaFrete(new BigDecimal(10.99d));
		restauranteRequest.setCozinha(cozinhaIdRequest);
		
		
		var cozinha = new Cozinha();
		cozinha.setId(10L);
		cozinha.setNome("Brasileira");
		
		var restaurante = new Restaurante();
		restaurante.setId(1L);
		restaurante.setNome("Borbulha");
		restaurante.setTaxaFrete(new BigDecimal(12.99d));
		restaurante.setCozinha(cozinha);
		
		
		mapper.copyToDomainObject(restauranteRequest, restaurante);
		
        assertThat(restaurante)
        	.isNotNull()
        	.extracting(Restaurante::getId, Restaurante::getNome, Restaurante::getTaxaFrete)
        	.containsExactly(1L, "Di Napoli", new BigDecimal(10.99d));
        
        assertThat(restaurante.getCozinha())
	    	.isNotNull()
	    	.extracting(Cozinha::getId, Cozinha::getNome)
	    	.containsExactly(20L, null);        
        
	}
}

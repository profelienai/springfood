package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CozinhaResponse;
import com.elienai.springfood.api.dto.RestauranteResponse;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;

public class RestauranteResponseMapperTest {

	private RestauranteResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
		mapper = new RestauranteResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterRestauranteParaRestauranteResponse() {
		var cozinha = new Cozinha();
		cozinha.setId(10L);
		cozinha.setNome("Brasileira");

		var restaurante = new Restaurante();
		restaurante.setId(1L);
		restaurante.setNome("Borbulha");
		restaurante.setTaxaFrete(new BigDecimal(10.99d));
		restaurante.setCozinha(cozinha);
		restaurante.ativar();
		
		var restauranteResponse = mapper.toResponse(restaurante);
		
		assertThat(restauranteResponse)
			.isNotNull()
			.extracting(RestauranteResponse::getId, RestauranteResponse::getNome, RestauranteResponse::getTaxaFrete, RestauranteResponse::getAtivo)
			.containsExactly(1L, "Borbulha", new BigDecimal(10.99d), Boolean.TRUE);
		
		assertThat(restauranteResponse.getCozinha())
			.isNotNull()
			.extracting(CozinhaResponse::getId, CozinhaResponse::getNome)
			.containsExactly(10L, "Brasileira");		
	}
	
	@Test
	void deveConverterCollectionDeRestauranteParaCollectionDeRestauranteResponse() {
		var restauranteBorbulha = new Restaurante();
		restauranteBorbulha.setId(1L);
		restauranteBorbulha.setTaxaFrete(new BigDecimal(10.99d));
		restauranteBorbulha.setNome("Borbulha");
		restauranteBorbulha.ativar();
		
		var restauranteDiNapoli = new Restaurante();
		restauranteDiNapoli.setId(2L);
		restauranteDiNapoli.setTaxaFrete(new BigDecimal(12.99d));
		restauranteDiNapoli.setNome("Di Napoli");
		restauranteDiNapoli.inativar();
		
		var restaurante = List.of(restauranteBorbulha, restauranteDiNapoli);
		
		var restaurantesResponse = mapper.toCollectionResponse(restaurante);
		
		assertThat(restaurantesResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(RestauranteResponse::getId, RestauranteResponse::getNome, RestauranteResponse::getTaxaFrete, RestauranteResponse::getAtivo)
			.containsExactlyInAnyOrder(
				tuple(1L, "Borbulha", new BigDecimal(10.99d), Boolean.TRUE),
				tuple(2L, "Di Napoli", new BigDecimal(12.99d), Boolean.FALSE));
	}
}

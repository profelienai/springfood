package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.RestauranteResponse;
import com.elienai.springfood.domain.model.Restaurante;

@Component
public class RestauranteResponseMapper {

	private ModelMapper modelMapper;
	
	public RestauranteResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public RestauranteResponse toResponse(Restaurante restaurante) {
		return modelMapper.map(restaurante, RestauranteResponse.class);
	}
	
	public List<RestauranteResponse> toCollectionResponse(List<Restaurante> restaurantes) {
		return restaurantes.stream()
				.map(restaurante -> toResponse(restaurante))
				.collect(Collectors.toList());
	}
}

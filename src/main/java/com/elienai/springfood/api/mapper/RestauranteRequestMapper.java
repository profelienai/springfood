package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.RestauranteRequest;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;

@Component
public class RestauranteRequestMapper {

	private ModelMapper modelMapper;
	
	public RestauranteRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Restaurante toDomainObject(RestauranteRequest restauranteRequest) {
		return modelMapper.map(restauranteRequest,Restaurante.class);
	}

	public void copyToDomainObject(RestauranteRequest restauranteRequest, Restaurante restaurante) {
		// Para evitar org.hibernate.HibernateException: identifier of an instance of 
		// com.elienai.springfood.domain.model.Cozinha was altered from 1 to 2
		restaurante.setCozinha(new Cozinha());
			
		modelMapper.map(restauranteRequest, restaurante);
	}
	
}

package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.EstadoRequest;
import com.elienai.springfood.domain.model.Estado;

@Component
public class EstadoRequestMapper {

	private ModelMapper modelMapper;
	
	public EstadoRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Estado toDomainObject(EstadoRequest estadoRequest) {
		return modelMapper.map(estadoRequest,Estado.class);
	}

	public void copyToDomainObject(EstadoRequest estadoRequest, Estado estado) {
		modelMapper.map(estadoRequest, estado);
	}
	
}

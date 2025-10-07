package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.CozinhaRequest;
import com.elienai.springfood.domain.model.Cozinha;

@Component
public class CozinhaRequestMapper {

	private ModelMapper modelMapper;
	
	public CozinhaRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Cozinha toDomainObject(CozinhaRequest cozinhaRequest) {
		return modelMapper.map(cozinhaRequest,Cozinha.class);
	}

	public void copyToDomainObject(CozinhaRequest cozinhaRequest, Cozinha cozinha) {
		modelMapper.map(cozinhaRequest, cozinha);
	}
	
}

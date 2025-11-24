package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.GrupoRequest;
import com.elienai.springfood.domain.model.Grupo;

@Component
public class GrupoRequestMapper {

	private ModelMapper modelMapper;
	
	public GrupoRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Grupo toDomainObject(GrupoRequest grupoRequest) {
		return modelMapper.map(grupoRequest,Grupo.class);
	}

	public void copyToDomainObject(GrupoRequest grupoRequest, Grupo grupo) {
		modelMapper.map(grupoRequest, grupo);
	}
	
}

package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.UsuarioRequest;
import com.elienai.springfood.domain.model.Usuario;

@Component
public class UsuarioRequestMapper {

	private ModelMapper modelMapper;
	
	public UsuarioRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Usuario toDomainObject(UsuarioRequest usuarioRequest) {
		return modelMapper.map(usuarioRequest,Usuario.class);
	}

	public void copyToDomainObject(UsuarioRequest usuarioRequest, Usuario usuario) {
		modelMapper.map(usuarioRequest, usuario);
	}
	
}

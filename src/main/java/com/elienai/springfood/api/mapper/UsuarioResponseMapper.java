package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.domain.model.Usuario;

@Component
public class UsuarioResponseMapper {

	private ModelMapper modelMapper;
	
	public UsuarioResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public UsuarioResponse toResponse(Usuario usuario) {
		return modelMapper.map(usuario, UsuarioResponse.class);
	}
	
	public List<UsuarioResponse> toCollectionResponse(List<Usuario> usuarios) {
		return usuarios.stream()
				.map(usuario -> toResponse(usuario))
				.collect(Collectors.toList());
	}
}

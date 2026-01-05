package com.elienai.springfood.api.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.GrupoResponse;
import com.elienai.springfood.domain.model.Grupo;

@Component
public class GrupoResponseMapper {

	private ModelMapper modelMapper;
	
	public GrupoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public GrupoResponse toResponse(Grupo grupo) {
		return modelMapper.map(grupo, GrupoResponse.class);
	}
	
	public List<GrupoResponse> toCollectionResponse(Collection<Grupo> grupos) {
		return grupos.stream()
				.map(grupo -> toResponse(grupo))
				.collect(Collectors.toList());
	}
}

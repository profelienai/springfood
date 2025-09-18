package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.EstadoResponse;
import com.elienai.springfood.domain.model.Estado;

@Component
public class EstadoResponseMapper {

	private ModelMapper modelMapper;
	
	public EstadoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public EstadoResponse toResponse(Estado estado) {
		return modelMapper.map(estado, EstadoResponse.class);
	}
	
	public List<EstadoResponse> toCollectionResponse(List<Estado> estados) {
		return estados.stream()
				.map(estado -> toResponse(estado))
				.collect(Collectors.toList());
	}
}

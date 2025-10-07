package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.CozinhaResponse;
import com.elienai.springfood.domain.model.Cozinha;

@Component
public class CozinhaResponseMapper {

	private ModelMapper modelMapper;
	
	public CozinhaResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public CozinhaResponse toResponse(Cozinha cozinha) {
		return modelMapper.map(cozinha, CozinhaResponse.class);
	}
	
	public List<CozinhaResponse> toCollectionResponse(List<Cozinha> cozinhas) {
		return cozinhas.stream()
				.map(cozinha -> toResponse(cozinha))
				.collect(Collectors.toList());
	}
}

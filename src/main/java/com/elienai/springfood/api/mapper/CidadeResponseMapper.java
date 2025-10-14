package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.CidadeResponse;
import com.elienai.springfood.domain.model.Cidade;

@Component
public class CidadeResponseMapper {

	private ModelMapper modelMapper;
	
	public CidadeResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public CidadeResponse toResponse(Cidade cidade) {
		return modelMapper.map(cidade, CidadeResponse.class);
	}
	
	public List<CidadeResponse> toCollectionResponse(List<Cidade> cidades) {
		return cidades.stream()
				.map(cidade -> toResponse(cidade))
				.collect(Collectors.toList());
	}
}

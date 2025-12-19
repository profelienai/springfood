package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.ProdutoResponse;
import com.elienai.springfood.domain.model.Produto;

@Component
public class ProdutoResponseMapper {

	private ModelMapper modelMapper;
	
	public ProdutoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public ProdutoResponse toResponse(Produto produto) {
		return modelMapper.map(produto, ProdutoResponse.class);
	}
	
	public List<ProdutoResponse> toCollectionResponse(List<Produto> produtos) {
		return produtos.stream()
				.map(produto -> toResponse(produto))
				.collect(Collectors.toList());
	}
}

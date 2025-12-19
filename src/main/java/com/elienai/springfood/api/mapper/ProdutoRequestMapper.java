package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.ProdutoRequest;
import com.elienai.springfood.domain.model.Produto;

@Component
public class ProdutoRequestMapper {

	private ModelMapper modelMapper;
	
	public ProdutoRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Produto toDomainObject(ProdutoRequest produtoRequest) {
		return modelMapper.map(produtoRequest,Produto.class);
	}

	public void copyToDomainObject(ProdutoRequest produtoRequest, Produto produto) {
		modelMapper.map(produtoRequest, produto);
	}
	
}

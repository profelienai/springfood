package com.elienai.springfood.api.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.PermissaoResponse;
import com.elienai.springfood.domain.model.Permissao;

@Component
public class PermissaoResponseMapper {

	private ModelMapper modelMapper;
	
	public PermissaoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public PermissaoResponse toResponse(Permissao permissao) {
		return modelMapper.map(permissao, PermissaoResponse.class);
	}
	
	public List<PermissaoResponse> toCollectionResponse(Collection<Permissao> permissoes) {
		return permissoes.stream()
				.map(permissao -> toResponse(permissao))
				.collect(Collectors.toList());
	}
}

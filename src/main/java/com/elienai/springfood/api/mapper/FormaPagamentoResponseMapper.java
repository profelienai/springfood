package com.elienai.springfood.api.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.domain.model.FormaPagamento;

@Component
public class FormaPagamentoResponseMapper {

	private ModelMapper modelMapper;
	
	public FormaPagamentoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public FormaPagamentoResponse toResponse(FormaPagamento formaPagamento) {
		return modelMapper.map(formaPagamento, FormaPagamentoResponse.class);
	}
	
	public List<FormaPagamentoResponse> toCollectionResponse(Collection<FormaPagamento> formasPagamento) {
		return formasPagamento.stream()
				.map(formaPagamento -> toResponse(formaPagamento))
				.collect(Collectors.toList());
	}
}

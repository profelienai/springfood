package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.FormaPagamentoRequest;
import com.elienai.springfood.domain.model.FormaPagamento;

@Component
public class FormaPagamentoRequestMapper {

	private ModelMapper modelMapper;
	
	public FormaPagamentoRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public FormaPagamento toDomainObject(FormaPagamentoRequest formaPagamentoRequest) {
		return modelMapper.map(formaPagamentoRequest,FormaPagamento.class);
	}

	public void copyToDomainObject(FormaPagamentoRequest formaPagamentoRequest, FormaPagamento formaPagamento) {
		modelMapper.map(formaPagamentoRequest, formaPagamento);
	}
	
}

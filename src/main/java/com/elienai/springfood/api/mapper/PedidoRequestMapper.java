package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.PedidoRequest;
import com.elienai.springfood.domain.model.Pedido;

@Component
public class PedidoRequestMapper {

	private ModelMapper modelMapper;
	
	public PedidoRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Pedido toDomainObject(PedidoRequest pedidoRequest) {
		return modelMapper.map(pedidoRequest,Pedido.class);
	}

	public void copyToDomainObject(PedidoRequest pedidoRequest, Pedido pedido) {
		modelMapper.map(pedidoRequest, pedido);
	}
	
}

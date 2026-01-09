package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.PedidoResponse;
import com.elienai.springfood.domain.model.Pedido;

@Component
public class PedidoResponseMapper {

	private ModelMapper modelMapper;
	
	public PedidoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public PedidoResponse toResponse(Pedido pedido) {
		return modelMapper.map(pedido, PedidoResponse.class);
	}
	
	public List<PedidoResponse> toCollectionResponse(List<Pedido> pedidos) {
		return pedidos.stream()
				.map(pedido -> toResponse(pedido))
				.collect(Collectors.toList());
	}
}

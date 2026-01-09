package com.elienai.springfood.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.PedidoResumoResponse;
import com.elienai.springfood.domain.model.Pedido;

@Component
public class PedidoResumoResponseMapper {

	private ModelMapper modelMapper;
	
	public PedidoResumoResponseMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public PedidoResumoResponse toResponse(Pedido pedido) {
		return modelMapper.map(pedido, PedidoResumoResponse.class);
	}
	
	public List<PedidoResumoResponse> toCollectionResponse(List<Pedido> pedidos) {
		return pedidos.stream()
				.map(pedido -> toResponse(pedido))
				.collect(Collectors.toList());
	}
}

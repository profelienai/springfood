package com.elienai.springfood.domain.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.model.StatusPedidoEnum;

@Service
public class FluxoPedidoService {

	@Autowired
	private EmissaoPedidoService emissaoPedido;
	
	@Transactional
	public void confirmar(Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
		
		if (!pedido.getStatus().equals(StatusPedidoEnum.CRIADO)) {
			throw new NegocioException(
					String.format("Status do pedido %d não pode ser alterado de %s para %s",
							pedido.getId(), pedido.getStatus().getDescricao(), 
							StatusPedidoEnum.CONFIRMADO.getDescricao()));
		}
		
		pedido.setStatus(StatusPedidoEnum.CONFIRMADO);
		pedido.setDataConfirmacao(OffsetDateTime.now());
	}
	
	@Transactional
	public void cancelar(Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
		
		if (!pedido.getStatus().equals(StatusPedidoEnum.CRIADO)) {
			throw new NegocioException(
					String.format("Status do pedido %d não pode ser alterado de %s para %s",
							pedido.getId(), pedido.getStatus().getDescricao(), 
							StatusPedidoEnum.CANCELADO.getDescricao()));
		}
		
		pedido.setStatus(StatusPedidoEnum.CANCELADO);
		pedido.setDataCancelamento(OffsetDateTime.now());
	}
	
	@Transactional
	public void entregar(Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
		
		if (!pedido.getStatus().equals(StatusPedidoEnum.CONFIRMADO)) {
			throw new NegocioException(
					String.format("Status do pedido %d não pode ser alterado de %s para %s",
							pedido.getId(), pedido.getStatus().getDescricao(), 
							StatusPedidoEnum.ENTREGUE.getDescricao()));
		}
		
		pedido.setStatus(StatusPedidoEnum.ENTREGUE);
		pedido.setDataEntrega(OffsetDateTime.now());
	}
	
}

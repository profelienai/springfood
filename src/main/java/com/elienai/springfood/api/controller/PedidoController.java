package com.elienai.springfood.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elienai.springfood.api.dto.PedidoResponse;
import com.elienai.springfood.api.dto.PedidoResumoResponse;
import com.elienai.springfood.api.mapper.PedidoResponseMapper;
import com.elienai.springfood.api.mapper.PedidoResumoResponseMapper;
import com.elienai.springfood.domain.model.Pedido;
import com.elienai.springfood.domain.repository.PedidoRepository;
import com.elienai.springfood.domain.service.EmissaoPedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private EmissaoPedidoService emissaoPedido;
	
	@Autowired
	private PedidoResponseMapper pedidoResponseMapper;
	
	@Autowired
	private PedidoResumoResponseMapper pedidoResumoResponseMapper;
	
	@GetMapping
	public List<PedidoResumoResponse> listar() {
		List<Pedido> todosPedidos = pedidoRepository.findAll();
		
		return pedidoResumoResponseMapper.toCollectionResponse(todosPedidos);
	}
	
	@GetMapping("/{pedidoId}")
	public PedidoResponse buscar(@PathVariable Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarOuFalhar(pedidoId);
		
		return pedidoResponseMapper.toResponse(pedido);
	}
	
}
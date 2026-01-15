package com.elienai.springfood.domain.model;

import java.util.Arrays;
import java.util.List;

public enum StatusPedidoEnum {

	CRIADO("Criado"),
	CONFIRMADO("Confirmado", CRIADO),
	ENTREGUE("Entregue", CONFIRMADO),
	CANCELADO("Cancelado", CRIADO);
	
	private String descricao;
	private List<StatusPedidoEnum> statusAnteriores;
	
	StatusPedidoEnum(String descricao, StatusPedidoEnum... statusAnteriores) {
		this.descricao = descricao;
		this.statusAnteriores = Arrays.asList(statusAnteriores);
	}
	
	public String getDescricao() {
		return this.descricao;
	}
	
	public boolean naoPodeAlterarPara(StatusPedidoEnum novoStatus) {
		return !novoStatus.statusAnteriores.contains(this);
	}
	
}
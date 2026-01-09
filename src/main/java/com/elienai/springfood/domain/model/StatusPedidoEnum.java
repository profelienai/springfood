package com.elienai.springfood.domain.model;

public enum StatusPedidoEnum {

	CRIADO("Criado"),
	CONFIRMADO("Confirmado"),
	ENTREGUE("Entregue"),
	CANCELADO("Cancelado");
	
	private String descricao;
	
	StatusPedidoEnum(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return this.descricao;
	}	
}
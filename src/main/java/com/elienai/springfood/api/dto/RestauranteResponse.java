package com.elienai.springfood.api.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestauranteResponse {

	private Long id;
	private String nome;
	private BigDecimal taxaFrete;
	private CozinhaResponse cozinha;
	private Boolean ativo;
	private Boolean aberto;
	private EnderecoResponse endereco;
	
}

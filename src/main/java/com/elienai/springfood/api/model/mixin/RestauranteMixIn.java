package com.elienai.springfood.api.model.mixin;

import java.time.OffsetDateTime;
import java.util.List;

import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Endereco;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.model.Produto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public abstract class RestauranteMixIn {

	@JsonIgnoreProperties(value = "nome", allowGetters = true)
	private Cozinha cozinha;
	
	@JsonIgnore
	private Endereco endereco;
	
	@JsonIgnore
	private OffsetDateTime dataCadastro;
	
	@JsonIgnore
	private OffsetDateTime dataAtualizacao;
	
	@JsonIgnore
	private List<FormaPagamento> formasPagamento; 
	
	@JsonIgnore
	private List<Produto> produtos;
}

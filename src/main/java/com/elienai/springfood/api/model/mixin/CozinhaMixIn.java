package com.elienai.springfood.api.model.mixin;

import java.util.List;

import com.elienai.springfood.domain.model.Restaurante;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class CozinhaMixIn {

	@JsonIgnore
	private List<Restaurante> restaurantes;
	
	
}

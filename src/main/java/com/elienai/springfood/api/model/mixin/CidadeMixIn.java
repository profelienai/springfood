package com.elienai.springfood.api.model.mixin;

import com.elienai.springfood.domain.model.Estado;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public abstract class CidadeMixIn {

	@JsonIgnoreProperties(value = "nome", allowGetters = true)
	private Estado estado;	
	
}

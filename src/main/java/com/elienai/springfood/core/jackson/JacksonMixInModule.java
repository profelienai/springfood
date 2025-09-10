package com.elienai.springfood.core.jackson;

import org.springframework.stereotype.Component;

import com.elienai.springfood.api.model.mixin.CidadeMixIn;
import com.elienai.springfood.api.model.mixin.CozinhaMixIn;
import com.elienai.springfood.api.model.mixin.RestauranteMixIn;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class JacksonMixInModule extends SimpleModule {

	private static final long serialVersionUID = 1L;

	public JacksonMixInModule() {
		setMixInAnnotation(Restaurante.class, RestauranteMixIn.class);
		setMixInAnnotation(Cozinha.class, CozinhaMixIn.class);
		setMixInAnnotation(Cidade.class, CidadeMixIn.class);
	}
}

package com.elienai.springfood.api.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CidadeIdRequest {
	@NotNull
	private Long id;
}

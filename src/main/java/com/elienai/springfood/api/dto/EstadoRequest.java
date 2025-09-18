package com.elienai.springfood.api.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstadoRequest {

	@NotBlank
	private String nome;
	
}

package com.elienai.springfood.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CidadeResponse {

	private Long id;
	private String nome;
	private EstadoResponse estado;
	
}

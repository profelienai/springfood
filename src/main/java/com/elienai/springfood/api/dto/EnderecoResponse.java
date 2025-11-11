package com.elienai.springfood.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EnderecoResponse {

	private String cep;
	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String estado;
	
}

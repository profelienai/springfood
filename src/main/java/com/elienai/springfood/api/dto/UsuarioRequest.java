package com.elienai.springfood.api.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsuarioRequest {

	@NotBlank
	private String nome;
	
	@NotBlank
	@Email
	private String email;
	
}

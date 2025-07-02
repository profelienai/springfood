package com.elienai.springfood.domain.repository;

import java.util.List;

import com.elienai.springfood.domain.model.Cozinha;

public interface CozinhaRepository {

	List<Cozinha> listar();
	Cozinha buscar(Long id);
	Cozinha salvar(Cozinha cozinha);
	void remover(Long id);
	
}

package com.elienai.springfood.domain.repository;

import java.util.List;

import com.elienai.springfood.domain.model.Cidade;

public interface CidadeRepository {

	List<Cidade> listar();
	Cidade buscar(Long id);
	Cidade salvar(Cidade cidade);
	void remover(Long id);
	
}

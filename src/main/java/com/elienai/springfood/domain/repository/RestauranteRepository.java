package com.elienai.springfood.domain.repository;

import java.util.List;

import com.elienai.springfood.domain.model.Restaurante;

public interface RestauranteRepository {

	List<Restaurante> listar();
	Restaurante buscar(Long id);
	Restaurante salvar(Restaurante restaurante);
	void remover(Long id);
	
}

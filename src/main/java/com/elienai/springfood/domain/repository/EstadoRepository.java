package com.elienai.springfood.domain.repository;

import java.util.List;

import com.elienai.springfood.domain.model.Estado;

public interface EstadoRepository {

	List<Estado> listar();
	Estado buscar(Long id);
	Estado salvar(Estado estado);
	void remover(Long id);
	
}

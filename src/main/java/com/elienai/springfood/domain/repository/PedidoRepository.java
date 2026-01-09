package com.elienai.springfood.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.elienai.springfood.domain.model.Pedido;

@Repository
public interface PedidoRepository extends CustomJpaRepository<Pedido, Long> {

	@Query("from Pedido p join fetch p.cliente join fetch p.restaurante r join fetch r.cozinha join fetch p.enderecoEntrega.cidade c join fetch c.estado order by p.id")
	List<Pedido> findAll();
	
}


package com.elienai.springfood.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.elienai.springfood.domain.model.Restaurante;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
	
	/**
	 * Durante a listagem de restaurantes, carregará a cozinha junto,
	 * evitando LazyInitializationException e a sobrecarga de 'selects' devido ao problema de N+1 queries.
	 */	
	@Query("from Restaurante r join fetch r.cozinha")
	List<Restaurante> findAll();
	
}

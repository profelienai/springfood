package com.elienai.springfood.api.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.CozinhaRepository;
import com.elienai.springfood.domain.repository.RestauranteRepository;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@GetMapping
	public List<Restaurante> listar() {
		return restauranteRepository.listar();
	}
	
	@GetMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> buscar(@PathVariable Long restauranteId) {
		Restaurante restaurante = restauranteRepository.buscar(restauranteId);
		
		if (restaurante != null) {
			return ResponseEntity.ok(restaurante);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) {
		try {
			restaurante = restauranteRepository.salvar(restaurante);
			
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(restaurante);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.badRequest()
					.body(e.getMessage());
		}
	}

	
	@PutMapping("/{restauranteId}")
	public ResponseEntity<?> atualizar(@PathVariable Long restauranteId,
			@RequestBody Restaurante restaurante) {
		Restaurante restauranteAtual = restauranteRepository.buscar(restauranteId);
		
		if (restauranteAtual != null) {
			Cozinha cozinha = cozinhaRepository.buscar(restaurante.getCozinha().getId());
			
			if (cozinha != null) {
				restaurante.setCozinha(cozinha);
			} else {
				return ResponseEntity.badRequest().build();
			}
			
			BeanUtils.copyProperties(restaurante, restauranteAtual, "id");
			
			restauranteAtual = restauranteRepository.salvar(restauranteAtual);
			return ResponseEntity.ok(restauranteAtual);
		}
		
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> remover(@PathVariable Long restauranteId) {
		try {
			restauranteRepository.remover(restauranteId);
			return ResponseEntity.noContent().build();
			
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}	
}

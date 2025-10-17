package com.elienai.springfood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.elienai.springfood.api.dto.RestauranteRequest;
import com.elienai.springfood.api.dto.RestauranteResponse;
import com.elienai.springfood.api.mapper.RestauranteRequestMapper;
import com.elienai.springfood.api.mapper.RestauranteResponseMapper;
import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.RestauranteRepository;
import com.elienai.springfood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroRestauranteService cadastroRestaurante;
	
	@Autowired
	private RestauranteRequestMapper restauranteRequestMapper;
	
	@Autowired
	private RestauranteResponseMapper restauranteResponseMapper;	
	
	@GetMapping
	public List<RestauranteResponse> listar() {
		List<Restaurante> restaurantes = restauranteRepository.findAll();
		return restauranteResponseMapper.toCollectionResponse(restaurantes);
	}
	
	@GetMapping("/{restauranteId}")
	public RestauranteResponse buscar(@PathVariable Long restauranteId) {
		Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(restauranteId);
		return restauranteResponseMapper.toResponse(restaurante);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteResponse adicionar(@RequestBody @Valid RestauranteRequest restauranteRequest) {
		try {
			Restaurante restaurante = restauranteRequestMapper.toDomainObject(restauranteRequest);
			restaurante = cadastroRestaurante.salvar(restaurante);
			return restauranteResponseMapper.toResponse(restaurante);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@PutMapping("/{restauranteId}")
	public RestauranteResponse atualizar(@PathVariable Long restauranteId, @RequestBody @Valid RestauranteRequest restauranteRequest) {
		Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
		restauranteRequestMapper.copyToDomainObject(restauranteRequest, restauranteAtual);
		
		try {
			restauranteAtual = cadastroRestaurante.salvar(restauranteAtual);
			return restauranteResponseMapper.toResponse(restauranteAtual);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@DeleteMapping("/{restauranteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long restauranteId) {
		cadastroRestaurante.excluir(restauranteId);
	}	
}

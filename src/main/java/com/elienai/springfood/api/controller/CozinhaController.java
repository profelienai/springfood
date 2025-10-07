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

import com.elienai.springfood.api.dto.CozinhaRequest;
import com.elienai.springfood.api.dto.CozinhaResponse;
import com.elienai.springfood.api.mapper.CozinhaRequestMapper;
import com.elienai.springfood.api.mapper.CozinhaResponseMapper;
import com.elienai.springfood.domain.model.Cozinha;
import com.elienai.springfood.domain.repository.CozinhaRepository;
import com.elienai.springfood.domain.service.CadastroCozinhaService;

@RestController
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private CadastroCozinhaService cadastroCozinha;
	
	@Autowired
	private CozinhaResponseMapper cozinhaResponseMapper;
	
	@Autowired
	private CozinhaRequestMapper cozinhaRequestMapper;
	
	@GetMapping
	public List<CozinhaResponse> listar() {
		List<Cozinha> todasCozinhas = cozinhaRepository.findAll();
		return cozinhaResponseMapper.toCollectionResponse(todasCozinhas);
	}
	
	@GetMapping("/{cozinhaId}")
	@ResponseStatus(HttpStatus.OK)
	public CozinhaResponse buscar(@PathVariable Long cozinhaId) {
		Cozinha cozinha = cadastroCozinha.buscarOuFalhar(cozinhaId);
		return cozinhaResponseMapper.toResponse(cozinha);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaResponse adicionar(@RequestBody @Valid CozinhaRequest cozinhaRequest) {
		Cozinha cozinha = cozinhaRequestMapper.toDomainObject(cozinhaRequest);
		cozinha = cadastroCozinha.salvar(cozinha);
		return cozinhaResponseMapper.toResponse(cozinha);
	}
	
	@PutMapping("/{cozinhaId}")
	public CozinhaResponse atualizar(@PathVariable Long cozinhaId,@RequestBody @Valid CozinhaRequest cozinhaRequest) {
		Cozinha cozinhaAtual = cadastroCozinha.buscarOuFalhar(cozinhaId);
		cozinhaRequestMapper.copyToDomainObject(cozinhaRequest, cozinhaAtual);
		cadastroCozinha.salvar(cozinhaAtual);
			
		return cozinhaResponseMapper.toResponse(cozinhaAtual);
	}
	
	@DeleteMapping("/{cozinhaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cozinhaId) {
		cadastroCozinha.excluir(cozinhaId);	
	}
	
}

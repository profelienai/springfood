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

import com.elienai.springfood.api.dto.EstadoRequest;
import com.elienai.springfood.api.dto.EstadoResponse;
import com.elienai.springfood.api.mapper.EstadoRequestMapper;
import com.elienai.springfood.api.mapper.EstadoResponseMapper;
import com.elienai.springfood.domain.model.Estado;
import com.elienai.springfood.domain.repository.EstadoRepository;
import com.elienai.springfood.domain.service.CadastroEstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private CadastroEstadoService cadastroEstado;
	
	@Autowired
	private EstadoResponseMapper estadoResponseMapper;
	
	@Autowired
	private EstadoRequestMapper estadoRequestMapper;
	
	@GetMapping
	public List<EstadoResponse> listar() {
		List<Estado> todosEstados = estadoRepository.findAll();
		return estadoResponseMapper.toCollectionResponse(todosEstados);
	}
	
	@GetMapping("/{estadoId}")
	public EstadoResponse buscar(@PathVariable Long estadoId) {
		Estado estado = cadastroEstado.buscarOuFalhar(estadoId);
		return estadoResponseMapper.toResponse(estado);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoResponse adicionar(@RequestBody @Valid EstadoRequest estadoRequest) {
		Estado estado = estadoRequestMapper.toDomainObject(estadoRequest);
		
		estado = cadastroEstado.salvar(estado);
		
		return estadoResponseMapper.toResponse(estado);
	}
	
	@PutMapping("/{estadoId}")
	public EstadoResponse atualizar(@PathVariable Long estadoId, @RequestBody @Valid EstadoRequest estadoRequest) {
		Estado estadoAtual = cadastroEstado.buscarOuFalhar(estadoId);

		estadoRequestMapper.copyToDomainObject(estadoRequest, estadoAtual);
			
		cadastroEstado.salvar(estadoAtual);
		
		return estadoResponseMapper.toResponse(estadoAtual);
	}
	
	@DeleteMapping("/{estadoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long estadoId) {
		cadastroEstado.excluir(estadoId);	
	}
	
}

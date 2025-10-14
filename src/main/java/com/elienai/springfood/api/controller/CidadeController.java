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

import com.elienai.springfood.api.dto.CidadeRequest;
import com.elienai.springfood.api.dto.CidadeResponse;
import com.elienai.springfood.api.mapper.CidadeRequestMapper;
import com.elienai.springfood.api.mapper.CidadeResponseMapper;
import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.repository.CidadeRepository;
import com.elienai.springfood.domain.service.CadastroCidadeService;

@RestController
@RequestMapping(value = "/cidades")
public class CidadeController {

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private CadastroCidadeService cadastroCidade;	
	
	@Autowired
	private CidadeRequestMapper cidadeRequestMapper;
	
	@Autowired
	private CidadeResponseMapper cidadeResponseMapper;
	
	@GetMapping
	public List<CidadeResponse> listar() {
		List<Cidade> cidades = cidadeRepository.findAll();
		return cidadeResponseMapper.toCollectionResponse(cidades); 
	}
	
	@GetMapping("/{cidadeId}")
	public CidadeResponse buscar(@PathVariable Long cidadeId) {
		Cidade cidade = cadastroCidade.buscarOuFalhar(cidadeId);
		return cidadeResponseMapper.toResponse(cidade);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeResponse adicionar(@RequestBody @Valid CidadeRequest cidadeRequest) {
		try {
			Cidade cidade = cidadeRequestMapper.toDomainObject(cidadeRequest);
			cidade = cadastroCidade.salvar(cidade);
			
			return cidadeResponseMapper.toResponse(cidade);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@PutMapping("/{cidadeId}")
	public CidadeResponse atualizar(@PathVariable Long cidadeId, @RequestBody @Valid CidadeRequest cidadeRequest) {
		Cidade cidadeAtual = cadastroCidade.buscarOuFalhar(cidadeId);

		try {
			cidadeRequestMapper.copyToDomainObject(cidadeRequest, cidadeAtual);
			cidadeAtual = cadastroCidade.salvar(cidadeAtual);
			
			return cidadeResponseMapper.toResponse(cidadeAtual);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@DeleteMapping("/{cidadeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cidadeId) {
		cadastroCidade.excluir(cidadeId);	
	}
}

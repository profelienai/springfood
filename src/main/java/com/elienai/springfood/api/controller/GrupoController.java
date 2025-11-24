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

import com.elienai.springfood.api.dto.GrupoRequest;
import com.elienai.springfood.api.dto.GrupoResponse;
import com.elienai.springfood.api.mapper.GrupoRequestMapper;
import com.elienai.springfood.api.mapper.GrupoResponseMapper;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.repository.GrupoRepository;
import com.elienai.springfood.domain.service.CadastroGrupoService;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

	@Autowired
	private GrupoRepository grupoRepository;
	
	@Autowired
	private CadastroGrupoService cadastroGrupo;
	
	@Autowired
	private GrupoResponseMapper grupoResponseMapper;
	
	@Autowired
	private GrupoRequestMapper grupoRequestMapper;
	
	@GetMapping
	public List<GrupoResponse> listar() {
		List<Grupo> todosGrupos = grupoRepository.findAll();
		return grupoResponseMapper.toCollectionResponse(todosGrupos);
	}
	
	@GetMapping("/{grupoId}")
	public GrupoResponse buscar(@PathVariable Long grupoId) {
		Grupo grupo = cadastroGrupo.buscarOuFalhar(grupoId);
		return grupoResponseMapper.toResponse(grupo);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GrupoResponse adicionar(@RequestBody @Valid GrupoRequest grupoRequest) {
		Grupo grupo = grupoRequestMapper.toDomainObject(grupoRequest);
		
		grupo = cadastroGrupo.salvar(grupo);
		
		return grupoResponseMapper.toResponse(grupo);
	}
	
	@PutMapping("/{grupoId}")
	public GrupoResponse atualizar(@PathVariable Long grupoId, @RequestBody @Valid GrupoRequest grupoRequest) {
		Grupo grupoAtual = cadastroGrupo.buscarOuFalhar(grupoId);

		grupoRequestMapper.copyToDomainObject(grupoRequest, grupoAtual);
			
		cadastroGrupo.salvar(grupoAtual);
		
		return grupoResponseMapper.toResponse(grupoAtual);
	}
	
	@DeleteMapping("/{grupoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long grupoId) {
		cadastroGrupo.excluir(grupoId);	
	}
	
}

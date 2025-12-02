package com.elienai.springfood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.elienai.springfood.api.dto.SenhaRequest;
import com.elienai.springfood.api.dto.UsuarioComSenhaRequest;
import com.elienai.springfood.api.dto.UsuarioRequest;
import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.api.mapper.UsuarioRequestMapper;
import com.elienai.springfood.api.mapper.UsuarioResponseMapper;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.repository.UsuarioRepository;
import com.elienai.springfood.domain.service.CadastroUsuarioService;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CadastroUsuarioService cadastroUsuario;
	
	@Autowired
	private UsuarioResponseMapper usuarioResponseMapper;
	
	@Autowired
	private UsuarioRequestMapper usuarioRequestMapper;
	
	@GetMapping
	public List<UsuarioResponse> listar() {
		List<Usuario> todosUsuarios = usuarioRepository.findAll();
		
		return usuarioResponseMapper.toCollectionResponse(todosUsuarios);
	}
	
	@GetMapping("/{usuarioId}")
	public UsuarioResponse buscar(@PathVariable Long usuarioId) {
		Usuario usuario = cadastroUsuario.buscarOuFalhar(usuarioId);
		
		return usuarioResponseMapper.toResponse(usuario);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse adicionar(@RequestBody @Valid UsuarioComSenhaRequest usuarioRequest) {
		Usuario usuario = usuarioRequestMapper.toDomainObject(usuarioRequest);
		usuario = cadastroUsuario.salvar(usuario);
		
		return usuarioResponseMapper.toResponse(usuario);
	}
	
	@PutMapping("/{usuarioId}")
	public UsuarioResponse atualizar(@PathVariable Long usuarioId, @RequestBody @Valid UsuarioRequest usuarioRequest) {
		Usuario usuarioAtual = cadastroUsuario.buscarOuFalhar(usuarioId);
		usuarioRequestMapper.copyToDomainObject(usuarioRequest, usuarioAtual);
		usuarioAtual = cadastroUsuario.salvar(usuarioAtual);
		
		return usuarioResponseMapper.toResponse(usuarioAtual);
	}
	
	@PutMapping("/{usuarioId}/senha")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void alterarSenha(@PathVariable Long usuarioId, @RequestBody @Valid SenhaRequest senha) {
		cadastroUsuario.alterarSenha(usuarioId, senha.getSenhaAtual(), senha.getNovaSenha());
	}
	
}

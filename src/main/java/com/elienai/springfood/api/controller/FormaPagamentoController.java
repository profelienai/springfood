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

import com.elienai.springfood.api.dto.FormaPagamentoRequest;
import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.api.mapper.FormaPagamentoRequestMapper;
import com.elienai.springfood.api.mapper.FormaPagamentoResponseMapper;
import com.elienai.springfood.domain.model.FormaPagamento;
import com.elienai.springfood.domain.repository.FormaPagamentoRepository;
import com.elienai.springfood.domain.service.CadastroFormaPagamentoService;

@RestController
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController {

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamento;
	
	@Autowired
	private FormaPagamentoResponseMapper formaPagamentoResponseMapper;
	
	@Autowired
	private FormaPagamentoRequestMapper formaPagamentoRequestMapper;
	
	@GetMapping
	public List<FormaPagamentoResponse> listar() {
		List<FormaPagamento> todasFormasPagamentos = formaPagamentoRepository.findAll();
		
		return formaPagamentoResponseMapper.toCollectionResponse(todasFormasPagamentos);
	}
	
	@GetMapping("/{formaPagamentoId}")
	public FormaPagamentoResponse buscar(@PathVariable Long formaPagamentoId) {
		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(formaPagamentoId);
		
		return formaPagamentoResponseMapper.toResponse(formaPagamento);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoResponse adicionar(@RequestBody @Valid FormaPagamentoRequest formaPagamentoRequest) {
		FormaPagamento formaPagamento = formaPagamentoRequestMapper.toDomainObject(formaPagamentoRequest);
		
		formaPagamento = cadastroFormaPagamento.salvar(formaPagamento);
		
		return formaPagamentoResponseMapper.toResponse(formaPagamento);
	}
	
	@PutMapping("/{formaPagamentoId}")
	public FormaPagamentoResponse atualizar(@PathVariable Long formaPagamentoId,
			@RequestBody @Valid FormaPagamentoRequest formaPagamentoRequest) {
		FormaPagamento formaPagamentoAtual = cadastroFormaPagamento.buscarOuFalhar(formaPagamentoId);
		
		formaPagamentoRequestMapper.copyToDomainObject(formaPagamentoRequest, formaPagamentoAtual);
		
		formaPagamentoAtual = cadastroFormaPagamento.salvar(formaPagamentoAtual);
		
		return formaPagamentoResponseMapper.toResponse(formaPagamentoAtual);
	}
	
	@DeleteMapping("/{formaPagamentoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long formaPagamentoId) {
		cadastroFormaPagamento.excluir(formaPagamentoId);	
	}
	
}

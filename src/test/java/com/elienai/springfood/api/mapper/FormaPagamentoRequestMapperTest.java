package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.FormaPagamentoRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.FormaPagamento;

public class FormaPagamentoRequestMapperTest {

	private FormaPagamentoRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new FormaPagamentoRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterFormaPagamentoRequestParaDominObject() {
		var formaPagamentoRequest = new FormaPagamentoRequest();
		formaPagamentoRequest.setDescricao("Dinheiro");
		
		var formaPagamento = mapper.toDomainObject(formaPagamentoRequest);
		
        assertThat(formaPagamento)
        	.isNotNull()
        	.extracting(FormaPagamento::getDescricao)
        	.isEqualTo("Dinheiro");
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var formaPagamentoRequest = new FormaPagamentoRequest();
		formaPagamentoRequest.setDescricao("Dinheiro");
		
		var formaPagamento = new FormaPagamento();
		formaPagamento.setId(1L);
		formaPagamento.setDescricao("Cartão de Crédito");
		
		mapper.copyToDomainObject(formaPagamentoRequest, formaPagamento);
		
        assertThat(formaPagamento)
        	.isNotNull()
        	.extracting(FormaPagamento::getId, FormaPagamento::getDescricao)
        	.containsExactly(1L, "Dinheiro");
	}
}

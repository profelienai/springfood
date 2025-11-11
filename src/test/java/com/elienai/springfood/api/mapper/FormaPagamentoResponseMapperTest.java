package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.FormaPagamentoResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.FormaPagamento;

public class FormaPagamentoResponseMapperTest {

	private FormaPagamentoResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new FormaPagamentoResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterFormaPagamentoParaFormaPagamentoResponse() {
		
		var formaPagamento = new FormaPagamento();
		formaPagamento.setId(1L);
		formaPagamento.setDescricao("Dinheiro");
		
		var formaPagamentoReponse = mapper.toResponse(formaPagamento);
		
		assertThat(formaPagamentoReponse)
			.isNotNull()
			.extracting(FormaPagamentoResponse::getId, FormaPagamentoResponse::getDescricao)
			.containsExactly(1L, "Dinheiro");
	}
	
	@Test
	void deveConverterCollectionDeFormaPagamentoParaCollectiondeFormaPagamentoResponse() {
		var dinheiro = new FormaPagamento();
		dinheiro.setId(1L);
		dinheiro.setDescricao("Dinheiro");
		
		var cartaoCredito = new FormaPagamento();
		cartaoCredito.setId(2L);
		cartaoCredito.setDescricao("Cartão de Crédito");
		
		var formasPagto = List.of(dinheiro, cartaoCredito);
		
		var formasPagtoResponse = mapper.toCollectionResponse(formasPagto);
		
		assertThat(formasPagtoResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(FormaPagamentoResponse::getId, FormaPagamentoResponse::getDescricao)
			.containsExactlyInAnyOrder(
				tuple(1L, "Dinheiro"),
				tuple(2L, "Cartão de Crédito"));
	}
}

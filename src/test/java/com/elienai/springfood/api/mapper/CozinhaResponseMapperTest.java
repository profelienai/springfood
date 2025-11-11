package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CozinhaResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cozinha;

public class CozinhaResponseMapperTest {

	private CozinhaResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new CozinhaResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterCozinhaParaCozinhaResponse() {
		
		var cozinha = new Cozinha();
		cozinha.setId(1L);
		cozinha.setNome("Brasileira");
		
		var cozinhaResponse = mapper.toResponse(cozinha);
		
		assertThat(cozinhaResponse)
			.isNotNull()
			.extracting(CozinhaResponse::getId, CozinhaResponse::getNome)
			.containsExactly(1L, "Brasileira");
	}
	
	@Test
	void deveConverterCollectionDeCozinhaParaCollectionDeCozinhaResponse() {
		var cozinhaBA = new Cozinha();
		cozinhaBA.setId(1L);
		cozinhaBA.setNome("Baiana");
		
		var cozinhaMG = new Cozinha();
		cozinhaMG.setId(2L);
		cozinhaMG.setNome("Mineira");
		
		var cozinhas = List.of(cozinhaBA, cozinhaMG);
		
		var cozinhasResponse = mapper.toCollectionResponse(cozinhas);
		
		assertThat(cozinhasResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(CozinhaResponse::getId, CozinhaResponse::getNome)
			.containsExactlyInAnyOrder(
				tuple(1L, "Baiana"),
				tuple(2L, "Mineira"));
	}
}

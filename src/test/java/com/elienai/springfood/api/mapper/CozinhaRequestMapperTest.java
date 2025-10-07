package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CozinhaRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cozinha;

public class CozinhaRequestMapperTest {

	private CozinhaRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new CozinhaRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterCozinhaRequestParaDominObject() {
		var cozinhaRequest = new CozinhaRequest();
		cozinhaRequest.setNome("Brasileira");
		
		var cozinha = mapper.toDomainObject(cozinhaRequest);
		
        assertThat(cozinha)
        	.isNotNull()
        	.extracting(Cozinha::getNome)
        	.isEqualTo("Brasileira");
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var cozinhaRequest = new CozinhaRequest();
		cozinhaRequest.setNome("Brasileira");
		
		var cozinha = new Cozinha();
		cozinha.setId(1L);
		cozinha.setNome("Italiana");
		
		mapper.copyToDomainObject(cozinhaRequest, cozinha);
		
        assertThat(cozinha)
        	.isNotNull()
        	.extracting(Cozinha::getId, Cozinha::getNome)
        	.containsExactly(1L, "Brasileira");
	}
}

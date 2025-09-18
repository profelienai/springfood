package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.EstadoRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Estado;

public class EstadoRequestMapperTest {

	private EstadoRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new EstadoRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterEstadoRequestParaDominObject() {
		var estadoRequest = new EstadoRequest();
		estadoRequest.setNome("São Paulo");
		
		var estado = mapper.toDomainObject(estadoRequest);
		
        assertThat(estado)
        	.isNotNull()
        	.extracting(Estado::getNome)
        	.isEqualTo("São Paulo");
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var estadoRequest = new EstadoRequest();
		estadoRequest.setNome("São Paulo");
		
		var estado = new Estado();
		estado.setId(1L);
		estado.setNome("Minas Gerais");
		
		mapper.copyToDomainObject(estadoRequest, estado);
		
        assertThat(estado)
        	.isNotNull()
        	.extracting(Estado::getId, Estado::getNome)
        	.containsExactly(1L, "São Paulo");
	}
}

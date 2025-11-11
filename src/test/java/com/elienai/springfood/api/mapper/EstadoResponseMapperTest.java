package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.EstadoResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Estado;

public class EstadoResponseMapperTest {

	private EstadoResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new EstadoResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterEstadoParaEstadoResponse() {
		
		var estado = new Estado();
		estado.setId(1L);
		estado.setNome("São Paulo");
		
		var estadoReponse = mapper.toResponse(estado);
		
		assertThat(estadoReponse)
			.isNotNull()
			.extracting(EstadoResponse::getId, EstadoResponse::getNome)
			.containsExactly(1L, "São Paulo");
	}
	
	@Test
	void deveConverterCollectionDeEstadoParaCollectiondeEstadoResponde() {
		var estadoSP = new Estado();
		estadoSP.setId(1L);
		estadoSP.setNome("São Paulo");
		
		var estadoRJ = new Estado();
		estadoRJ.setId(2L);
		estadoRJ.setNome("Rio de Janeiro");
		
		var estados = List.of(estadoSP, estadoRJ);
		
		var estadosResponde = mapper.toCollectionResponse(estados);
		
		assertThat(estadosResponde)
			.isNotNull()
			.hasSize(2)
			.extracting(EstadoResponse::getId, EstadoResponse::getNome)
			.containsExactlyInAnyOrder(
				tuple(1L, "São Paulo"),
				tuple(2L, "Rio de Janeiro"));
	}
}

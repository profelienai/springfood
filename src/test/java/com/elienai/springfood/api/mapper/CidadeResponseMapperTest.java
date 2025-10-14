package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CidadeResponse;
import com.elienai.springfood.api.dto.EstadoResponse;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Estado;

public class CidadeResponseMapperTest {

	private CidadeResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
		mapper = new CidadeResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterCidadeParaCidadeResponse() {
		var estado = new Estado();
		estado.setId(10L);
		estado.setNome("São Paulo");

		var cidade = new Cidade();
		cidade.setId(1L);
		cidade.setNome("Pindamonhangaba");
		cidade.setEstado(estado);
		
		var cidadeResponse = mapper.toResponse(cidade);
		
		assertThat(cidadeResponse)
			.isNotNull()
			.extracting(CidadeResponse::getId, CidadeResponse::getNome)
			.containsExactly(1L, "Pindamonhangaba");
		
		assertThat(cidadeResponse.getEstado())
			.isNotNull()
			.extracting(EstadoResponse::getId, EstadoResponse::getNome)
			.containsExactly(10L, "São Paulo");		
	}
	
	@Test
	void deveConverterCollectionDeCidadeParaCollectionDeCidadeResponse() {
		var cidadeSP = new Cidade();
		cidadeSP.setId(1L);
		cidadeSP.setNome("Taubaté");
		
		var cidadeRJ = new Cidade();
		cidadeRJ.setId(2L);
		cidadeRJ.setNome("Resende");
		
		var cidades = List.of(cidadeSP, cidadeRJ);
		
		var cidadesResponse = mapper.toCollectionResponse(cidades);
		
		assertThat(cidadesResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(CidadeResponse::getId, CidadeResponse::getNome)
			.containsExactlyInAnyOrder(
				tuple(1L, "Taubaté"),
				tuple(2L, "Resende"));
	}
}

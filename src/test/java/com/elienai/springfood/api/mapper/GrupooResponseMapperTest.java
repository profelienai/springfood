package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.GrupoResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Grupo;

public class GrupooResponseMapperTest {

	private GrupoResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new GrupoResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterGrupoParaGrupoResponse() {
		
		var grupo = new Grupo();
		grupo.setId(1L);
		grupo.setNome("Vendedor");
		
		var grupoReponse = mapper.toResponse(grupo);
		
		assertThat(grupoReponse)
			.isNotNull()
			.extracting(GrupoResponse::getId, GrupoResponse::getNome)
			.containsExactly(1L, "Vendedor");
	}
	
	@Test
	void deveConverterCollectionDeGrupoParaCollectionDeGrupoResponse() {
		var grupoSP = new Grupo();
		grupoSP.setId(1L);
		grupoSP.setNome("Vendedor");
		
		var grupoRJ = new Grupo();
		grupoRJ.setId(2L);
		grupoRJ.setNome("Gerente");
		
		var grupos = List.of(grupoSP, grupoRJ);
		
		var gruposResponse = mapper.toCollectionResponse(grupos);
		
		assertThat(gruposResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(GrupoResponse::getId, GrupoResponse::getNome)
			.containsExactlyInAnyOrder(
				tuple(1L, "Vendedor"),
				tuple(2L, "Gerente"));
	}
}

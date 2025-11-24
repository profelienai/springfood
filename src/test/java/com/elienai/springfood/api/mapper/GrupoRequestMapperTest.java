package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.GrupoRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Grupo;

public class GrupoRequestMapperTest {

	private GrupoRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new GrupoRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterGrupoRequestParaDominObject() {
		var grupoRequest = new GrupoRequest();
		grupoRequest.setNome("Vendedor");
		
		var grupo = mapper.toDomainObject(grupoRequest);
		
        assertThat(grupo)
        	.isNotNull()
        	.extracting(Grupo::getNome)
        	.isEqualTo("Vendedor");
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var grupoRequest = new GrupoRequest();
		grupoRequest.setNome("Vendedor");
		
		var grupo = new Grupo();
		grupo.setId(1L);
		grupo.setNome("Gerente");
		
		mapper.copyToDomainObject(grupoRequest, grupo);
		
        assertThat(grupo)
        	.isNotNull()
        	.extracting(Grupo::getId, Grupo::getNome)
        	.containsExactly(1L, "Vendedor");
	}
}

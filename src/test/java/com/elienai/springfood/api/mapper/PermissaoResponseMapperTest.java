package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.PermissaoResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Permissao;

public class PermissaoResponseMapperTest {

	private PermissaoResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new PermissaoResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterPermissaoParaPermissaoResponse() {
		
		var permissao = new Permissao();
		permissao.setId(1L);
		permissao.setNome("CONSULTAR_COZINHAS");
		permissao.setDescricao("Permite consultar cozinhas");
		
		var permissaoReponse = mapper.toResponse(permissao);
		
		assertThat(permissaoReponse)
			.isNotNull()
			.extracting(PermissaoResponse::getId, PermissaoResponse::getNome, PermissaoResponse::getDescricao)
			.containsExactly(1L, "CONSULTAR_COZINHAS", "Permite consultar cozinhas");
	}
	
	@Test
	void deveConverterCollectionDePermissaoParaCollectionDePermissaoResponse() {
		var permissao1 = new Permissao();
		permissao1.setId(1L);
		permissao1.setNome("CONSULTAR_COZINHAS");
		permissao1.setDescricao("Permite consultar cozinhas");
		
		var permissao2 = new Permissao();
		permissao2.setId(2L);
		permissao2.setNome("EDITAR_COZINHAS");
		permissao2.setDescricao("Permite editar cozinhas");
		
		var permissaos = List.of(permissao1, permissao2);
		
		var permissaosResponse = mapper.toCollectionResponse(permissaos);
		
		assertThat(permissaosResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(PermissaoResponse::getId, PermissaoResponse::getNome, PermissaoResponse::getDescricao)
			.containsExactlyInAnyOrder(
				tuple(1L, "CONSULTAR_COZINHAS", "Permite consultar cozinhas" ),
				tuple(2L, "EDITAR_COZINHAS", "Permite editar cozinhas"));
	}
}

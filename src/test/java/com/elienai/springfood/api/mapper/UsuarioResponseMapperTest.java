package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Usuario;

public class UsuarioResponseMapperTest {

	private UsuarioResponseMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new UsuarioResponseMapper(modelMapper);
	}
	
	@Test
	void deveConverterUsuarioParaUsuarioResponse() {
		
		var usuario = new Usuario();
		usuario.setId(1L);
		usuario.setNome("José da Silva");
		usuario.setEmail("jose@gmail.com");
		
		var usuarioReponse = mapper.toResponse(usuario);
		
		assertThat(usuarioReponse)
			.isNotNull()
			.extracting(UsuarioResponse::getId, UsuarioResponse::getNome, UsuarioResponse::getEmail)
			.containsExactly(1L, "José da Silva", "jose@gmail.com");
	}
	
	@Test
	void deveConverterCollectionDeUsuarioParaCollectionDeUsuarioResponse() {
		var usuario1 = new Usuario();
		usuario1.setId(1L);
		usuario1.setNome("José da Silva");
		usuario1.setEmail("jose@gmail.com");
		
		var usuario2 = new Usuario();
		usuario2.setId(2L);
		usuario2.setNome("Sebastiao de Paula");
		usuario2.setEmail("sebastiao@gmail.com");
		
		var usuarios = List.of(usuario1, usuario2);
		
		var usuariosResponse = mapper.toCollectionResponse(usuarios);
		
		assertThat(usuariosResponse)
			.isNotNull()
			.hasSize(2)
			.extracting(UsuarioResponse::getId, UsuarioResponse::getNome, UsuarioResponse::getEmail)
			.containsExactlyInAnyOrder(
				tuple(1L, "José da Silva", "jose@gmail.com"),
				tuple(2L, "Sebastiao de Paula", "sebastiao@gmail.com"));
	}
}

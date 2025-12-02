package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.UsuarioRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Usuario;

public class UsuarioRequestMapperTest {

	private UsuarioRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new UsuarioRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterUsuarioRequestParaDominObject() {
		var usuarioRequest = new UsuarioRequest();
		usuarioRequest.setNome("José da Silva");
		usuarioRequest.setEmail("jose@gmail.com");
		
		var usuario = mapper.toDomainObject(usuarioRequest);
		
        assertThat(usuario)
        	.isNotNull()
        	.extracting(Usuario::getNome, Usuario::getEmail)
        	.containsExactly("José da Silva", "jose@gmail.com");
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var usuarioRequest = new UsuarioRequest();
		usuarioRequest.setNome("Sebastiao de Paula");
		usuarioRequest.setEmail("sebastiao@gmail.com");
		
		var usuario = new Usuario();
		usuario.setId(1L);
		usuario.setNome("José da Silva");
		usuario.setEmail("jose@gmail.com");
		
		mapper.copyToDomainObject(usuarioRequest, usuario);
		
        assertThat(usuario)
        	.isNotNull()
        	.extracting(Usuario::getId, Usuario::getNome, Usuario::getEmail)
        	.containsExactly(1L, "Sebastiao de Paula", "sebastiao@gmail.com");
	}
}

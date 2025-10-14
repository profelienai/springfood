package com.elienai.springfood.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.elienai.springfood.api.dto.CidadeRequest;
import com.elienai.springfood.api.dto.EstadoIdRequest;
import com.elienai.springfood.core.modelmapper.ModelMapperConfig;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Estado;

public class CidadeRequestMapperTest {
	private CidadeRequestMapper mapper;
	private ModelMapper modelMapper;
	
	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapperConfig().modelMapper();
		mapper = new CidadeRequestMapper(modelMapper);
	}
	
	@Test
	void deveConverterCidadeRequestParaDominObject() {
		var estadoIdRequest = new EstadoIdRequest();
		estadoIdRequest.setId(10L);

		var cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome("Pindamonhangaba");
		cidadeRequest.setEstado(estadoIdRequest);
		
		var cidade = mapper.toDomainObject(cidadeRequest);
		
        assertThat(cidade)
        	.isNotNull()
        	.extracting(Cidade::getId, Cidade::getNome)
        	.containsExactly(null, "Pindamonhangaba");
        
        assertThat(cidade.getEstado())
	    	.isNotNull()
	    	.extracting(Estado::getId, Estado::getNome)
	    	.containsExactly(10L, null);        
	}
	
	@Test
	void deveCopiarPropriedadesDeRequestParaDomainExistente() {
		var estadoIdRequest = new EstadoIdRequest();
		estadoIdRequest.setId(20L);
		
		var cidadeRequest = new CidadeRequest();
		cidadeRequest.setNome("Belo Horizonte");
		cidadeRequest.setEstado(estadoIdRequest);
		
		
		var estado = new Estado();
		estado.setId(10L);
		estado.setNome("São Paulo");
		
		var cidade = new Cidade();
		cidade.setId(1L);
		cidade.setNome("Guarulhos");
		cidade.setEstado(estado);
		
		
		mapper.copyToDomainObject(cidadeRequest, cidade);
		
        assertThat(cidade)
        	.isNotNull()
        	.extracting(Cidade::getId, Cidade::getNome)
        	.containsExactly(1L, "Belo Horizonte");
        
        assertThat(cidade.getEstado())
	    	.isNotNull()
	    	.extracting(Estado::getId, Estado::getNome)
	    	.containsExactly(20L, null);        
        
	}
}

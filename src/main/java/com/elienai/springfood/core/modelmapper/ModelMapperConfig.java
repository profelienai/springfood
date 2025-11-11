package com.elienai.springfood.core.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.elienai.springfood.api.dto.EnderecoResponse;
import com.elienai.springfood.domain.model.Endereco;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		var modelMapper = new ModelMapper();
		
		var enderecoTypeMap = modelMapper.createTypeMap(Endereco.class, EnderecoResponse.class);
		
		enderecoTypeMap.addMappings(mapper -> {
			mapper.map(endereco -> endereco.getCidade().getNome(), EnderecoResponse::setCidade);
			mapper.map(endereco -> endereco.getCidade().getEstado().getNome(), EnderecoResponse::setEstado);
		});
		
		return modelMapper;
	}
	
}

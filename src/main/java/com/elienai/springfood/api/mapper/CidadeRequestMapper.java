package com.elienai.springfood.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.elienai.springfood.api.dto.CidadeRequest;
import com.elienai.springfood.domain.model.Cidade;
import com.elienai.springfood.domain.model.Estado;

@Component
public class CidadeRequestMapper {

	private ModelMapper modelMapper;
	
	public CidadeRequestMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Cidade toDomainObject(CidadeRequest cidadeRequest) {
		return modelMapper.map(cidadeRequest,Cidade.class);
	}

	public void copyToDomainObject(CidadeRequest cidadeRequest, Cidade cidade) {
		// Para evitar org.hibernate.HibernateException: identifier of an instance of 
		// com.elienai.springfood.domain.model.Estado was altered from 1 to 2
		cidade.setEstado(new Estado());
			
		modelMapper.map(cidadeRequest, cidade);
	}
	
}

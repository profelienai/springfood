package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.elienai.springfood.domain.exception.EntidadeEmUsoException;
import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.repository.GrupoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroGrupoServiceTest {

	@Mock
	private GrupoRepository grupoRepository;
	
	@InjectMocks
	private CadastroGrupoService cadastroGrupoService;
	
	private Grupo grupo;
	
	@BeforeEach
	void setUp() {
		grupo = new Grupo();
		grupo.setId(1L);
	}
	
	@Test
	void testSalvar_ComSucesso() {
		when(grupoRepository.save(grupo)).thenReturn(grupo);
		
		Grupo grupoSalvo = cadastroGrupoService.salvar(grupo);
		
		assertNotNull(grupoSalvo);
		assertSame(grupo, grupoSalvo);
		
		verify(grupoRepository).save(grupo);
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long grupoId = 10L;
		
		doNothing().when(grupoRepository).deleteById(grupoId);
		
		assertDoesNotThrow(() -> cadastroGrupoService.excluir(grupoId));
		verify(grupoRepository).deleteById(grupoId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoGrupoNaoExiste() {
		Long grupoId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(grupoRepository).deleteById(grupoId);
		
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroGrupoService.excluir(grupoId));
		
		assertEquals("Não existe um cadastro de grupo com código 999", ex.getMessage());
		verify(grupoRepository).deleteById(grupoId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoGrupoEstaEmUso() {
		Long grupoId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(grupoRepository).deleteById(grupoId);
		
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroGrupoService.excluir(grupoId));
		
		assertEquals("Grupo de código 10 não pode ser removido, pois está em uso", ex.getMessage());
		verify(grupoRepository).deleteById(grupoId);		
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long grupoId = 10L;
		
		when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
		
		Grupo grupoEncontrado = cadastroGrupoService.buscarOuFalhar(grupoId);
	
		assertNotNull(grupoEncontrado);
		assertSame(grupo, grupoEncontrado);
		
		verify(grupoRepository).findById(grupoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoGrupoNaoExiste() {
		Long grupoId = 999L;
		
		when(grupoRepository.findById(grupoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroGrupoService.buscarOuFalhar(grupoId));
		
		assertEquals("Não existe um cadastro de grupo com código 999", ex.getMessage());
		verify(grupoRepository).findById(grupoId);
	}	
}

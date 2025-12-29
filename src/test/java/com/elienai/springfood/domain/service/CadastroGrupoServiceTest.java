package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.elienai.springfood.domain.exception.PermissaoNaoEncontradaException;
import com.elienai.springfood.domain.model.Grupo;
import com.elienai.springfood.domain.model.Permissao;
import com.elienai.springfood.domain.repository.GrupoRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroGrupoServiceTest {

	@Mock
	private GrupoRepository grupoRepository;
	
	@Mock
	private CadastroPermissaoService cadastroPermissao;
	
	@InjectMocks
	private CadastroGrupoService cadastroGrupo;
	
	private Grupo grupo;
	private Permissao permissao;
	
	@BeforeEach
	void setUp() {
		grupo = new Grupo();
		grupo.setId(10L);
		
		permissao = new Permissao();
		permissao.setId(100L);
	}
	
	@Test
	void testSalvar_ComSucesso() {
		when(grupoRepository.save(grupo)).thenReturn(grupo);
		
		Grupo grupoSalvo = cadastroGrupo.salvar(grupo);
		
		assertNotNull(grupoSalvo);
		assertSame(grupo, grupoSalvo);
		
		verify(grupoRepository).save(grupo);
	}
	
	@Test
	void testExcluir_ComSucesso() {
		Long grupoId = 10L;
		
		doNothing().when(grupoRepository).deleteById(grupoId);
		
		assertDoesNotThrow(() -> cadastroGrupo.excluir(grupoId));
		verify(grupoRepository).deleteById(grupoId);		
	}
	
	@Test
	void testExluir_LancarExcecaoQuandoGrupoNaoExiste() {
		Long grupoId = 999L;

		doThrow(new EmptyResultDataAccessException(1)).when(grupoRepository).deleteById(grupoId);
		
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroGrupo.excluir(grupoId));
		
		assertEquals("Não existe um cadastro de grupo com código 999", ex.getMessage());
		verify(grupoRepository).deleteById(grupoId);
		
	}
	
	@Test
	void testExcluir_LancarExcecaoQuandoGrupoEstaEmUso() {
		Long grupoId = 10L;

		doThrow(new DataIntegrityViolationException("")).when(grupoRepository).deleteById(grupoId);
		
		
		EntidadeEmUsoException ex =
				assertThrows(EntidadeEmUsoException.class, () -> cadastroGrupo.excluir(grupoId));
		
		assertEquals("Grupo de código 10 não pode ser removido, pois está em uso", ex.getMessage());
		verify(grupoRepository).deleteById(grupoId);		
	}
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long grupoId = 10L;
		
		when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
		
		Grupo grupoEncontrado = cadastroGrupo.buscarOuFalhar(grupoId);
	
		assertNotNull(grupoEncontrado);
		assertSame(grupo, grupoEncontrado);
		
		verify(grupoRepository).findById(grupoId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoGrupoNaoExiste() {
		Long grupoId = 999L;
		
		when(grupoRepository.findById(grupoId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroGrupo.buscarOuFalhar(grupoId));
		
		assertEquals("Não existe um cadastro de grupo com código 999", ex.getMessage());
		verify(grupoRepository).findById(grupoId);
	}
	
	@Test
	void testAssociarPermissao_ComSucesso() {
	    Long grupoId = 10L;
	    Long permissaoId = 100L;

	    when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
	    when(cadastroPermissao.buscarOuFalhar(permissaoId)).thenReturn(permissao);

	    assertDoesNotThrow(() -> cadastroGrupo.associarPermissao(grupoId, permissaoId));

	    assertTrue(grupo.getPermissoes().contains(permissao));

	    verify(grupoRepository).findById(grupoId);
	    verify(cadastroPermissao).buscarOuFalhar(permissaoId);
	}
	
	@Test
	void testAssociarPermissao_LancarExcecaoQuandoGrupoNaoExiste() {
	    Long grupoId = 999L;
	    Long permissaoId = 100L;

	    when(grupoRepository.findById(grupoId)).thenReturn(Optional.empty());

	    EntidadeNaoEncontradaException ex =
	        assertThrows(EntidadeNaoEncontradaException.class, 
	            () -> cadastroGrupo.associarPermissao(grupoId, permissaoId));

	    assertEquals("Não existe um cadastro de grupo com código 999", ex.getMessage());
	    verify(grupoRepository).findById(grupoId);
	}
	
	@Test
	void testAssociarPermissao_LancarExcecaoQuandoPermissaoNaoExiste() {
	    Long grupoId = 10L;
	    Long permissaoId = 999L;

	    when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
	    when(cadastroPermissao.buscarOuFalhar(permissaoId))
	        .thenThrow(new PermissaoNaoEncontradaException(permissaoId));

	    EntidadeNaoEncontradaException ex =
	    		assertThrows(EntidadeNaoEncontradaException.class, 
	    				() -> cadastroGrupo.associarPermissao(grupoId, permissaoId));

	    assertEquals("Não existe um cadastro de permissão com código 999", ex.getMessage());
	    verify(grupoRepository).findById(grupoId);
	    verify(cadastroPermissao).buscarOuFalhar(permissaoId);
	}
	
	@Test
	void testDesassociarPermissao_ComSucesso() {
	    Long grupoId = 10L;
	    Long permissaoId = 100L;

	    grupo.getPermissoes().add(permissao);

	    when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
	    when(cadastroPermissao.buscarOuFalhar(permissaoId)).thenReturn(permissao);

	    assertDoesNotThrow(() -> cadastroGrupo.desassociarPermissao(grupoId, permissaoId));

	    assertFalse(grupo.getPermissoes().contains(permissao));

	    verify(grupoRepository).findById(grupoId);
	    verify(cadastroPermissao).buscarOuFalhar(permissaoId);
	}

	@Test
	void testDesassociarPermissao_LancarExcecaoQuandoGrupoNaoExiste() {
	    Long grupoId = 999L;
	    Long permissaoId = 100L;

	    when(grupoRepository.findById(grupoId)).thenReturn(Optional.empty());

	    EntidadeNaoEncontradaException ex =
	        assertThrows(EntidadeNaoEncontradaException.class,
	            () -> cadastroGrupo.desassociarPermissao(grupoId, permissaoId));

	    assertEquals("Não existe um cadastro de grupo com código 999", ex.getMessage());
	    verify(grupoRepository).findById(grupoId);
	}
	
	@Test
	void testDesassociarPermissao_LancarExcecaoQuandoPermissaoNaoExiste() {
	    Long grupoId = 10L;
	    Long permissaoId = 999L;

	    when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
	    when(cadastroPermissao.buscarOuFalhar(permissaoId))
	        .thenThrow(new PermissaoNaoEncontradaException(permissaoId));

	    EntidadeNaoEncontradaException ex =
	    		assertThrows(EntidadeNaoEncontradaException.class,
	    				() -> cadastroGrupo.desassociarPermissao(grupoId, permissaoId));

	    assertEquals("Não existe um cadastro de permissão com código 999", ex.getMessage());
	    verify(grupoRepository).findById(grupoId);
	    verify(cadastroPermissao).buscarOuFalhar(permissaoId);
	}
		
}

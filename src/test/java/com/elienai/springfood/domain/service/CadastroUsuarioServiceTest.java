package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.elienai.springfood.domain.exception.EntidadeNaoEncontradaException;
import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class CadastroUsuarioServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;
	
	@InjectMocks
	private CadastroUsuarioService cadastroUsuarioService;
	
	private Usuario usuario;
	
	@BeforeEach
	void setUp() {
		usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail("usuario@gmail.com");
		usuario.setSenha("123");
	}
	
	@Test
	void testSalvar_ComSucesso() {
		Optional<Usuario> optUsuario = Optional.empty();
		
		when(usuarioRepository.save(usuario)).thenReturn(usuario);
		when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(optUsuario);
		
		Usuario usuarioSalvo = cadastroUsuarioService.salvar(usuario);
		
		assertNotNull(usuarioSalvo);
		assertSame(usuario, usuarioSalvo);
		
		verify(usuarioRepository).save(usuario);
		verify(usuarioRepository).findByEmail(usuario.getEmail());
	}
	
	@Test
	void testSalvar_LancarExcecao_EmailEmUso() {
		Optional<Usuario> optUsuario = Optional.of(new Usuario());
		
		when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(optUsuario);
		
		NegocioException ex = assertThrows(NegocioException.class, () -> cadastroUsuarioService.salvar(usuario));
		
		assertEquals("Já existe um usuário cadastrado com o e-mail usuario@gmail.com", ex.getMessage());
		
		verify(usuarioRepository).findByEmail(usuario.getEmail());		
	}	
	
	@Test
	void testBuscarOuFalhar_ComSucesso() {
		Long usuarioId = usuario.getId();
		
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		
		Usuario usuarioEncontrado = cadastroUsuarioService.buscarOuFalhar(usuarioId);
	
		assertNotNull(usuarioEncontrado);
		assertSame(usuario, usuarioEncontrado);
		
		verify(usuarioRepository).findById(usuarioId);
	}
	
	@Test
	void testBuscarOuFalhar_LancarExcecaoQuandoUsuarioNaoExiste() {
		Long usuarioId = 999L;
		
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
		
		EntidadeNaoEncontradaException ex =
				assertThrows(EntidadeNaoEncontradaException.class, () -> cadastroUsuarioService.buscarOuFalhar(usuarioId));
		
		assertEquals("Não existe um cadastro de usuário com código 999", ex.getMessage());
		verify(usuarioRepository).findById(usuarioId);
	}
	
	@Test
	void testAlterarSenha_ComSucesso() {
		Long usuarioId = usuario.getId();
		String senhaAtual = usuario.getSenha();
		String senhaNova = "nova123";
		
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		
		cadastroUsuarioService.alterarSenha(usuarioId, senhaAtual, senhaNova);

		assertEquals(senhaNova, usuario.getSenha());
		verify(usuarioRepository).findById(usuarioId);
	}
	
	@Test
	void testAlterarSenha_LancarExcecaoQuandoSenhaNaoCoincide() {
		Long usuarioId = usuario.getId();
		String senhaAtual = "senhaIncorreta";
		String senhaNova = "nova123";
		
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		
		NegocioException ex = assertThrows(NegocioException.class, () -> cadastroUsuarioService.alterarSenha(usuarioId, senhaAtual, senhaNova));

		assertEquals("Senha atual informada não coincide com a senha do usuário.", ex.getMessage());
		assertEquals("123", usuario.getSenha());
		verify(usuarioRepository).findById(usuarioId);
	}
}

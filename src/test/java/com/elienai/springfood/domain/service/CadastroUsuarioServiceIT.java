package com.elienai.springfood.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.elienai.springfood.domain.exception.NegocioException;
import com.elienai.springfood.domain.exception.UsuarioNaoEncontradoException;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.repository.UsuarioRepository;
import com.elienai.springfood.util.DatabaseCleaner;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(scripts = "/sql/CadastroUsuarioServiceIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CadastroUsuarioServiceIT {

	@Autowired
	private CadastroUsuarioService cadastroUsuario;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    /**
     * Limpa o banco após cada teste, garantindo isolamento entre os testes.
     */
    @AfterEach
    private void cleanDatabase() {
    	databaseCleaner.clearTables();
    }
    
	@Test
	public void deveRetornarUsuarioPorId() {
		Usuario usuario = cadastroUsuario.buscarOuFalhar(1L);
		
		assertNotNull(usuario);
		assertEquals("João da Silva", usuario.getNome());
	}
	
	@Test
	public void deveSalvarUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNome("Joaquim de Souza");
		usuario.setEmail("joaquim@gmail.com");
		usuario.setSenha("senha123");
		
		Usuario usuarioSalvo = cadastroUsuario.salvar(usuario);

		// Valida o objeto salvo
		assertNotNull(usuarioSalvo);
		assertNotNull(usuarioSalvo.getId());
		assertEquals("Joaquim de Souza", usuarioSalvo.getNome());
		assertEquals("joaquim@gmail.com", usuarioSalvo.getEmail());
		assertEquals("senha123", usuarioSalvo.getSenha());
		assertNotNull(usuarioSalvo.getDataCadastro());
		
		// Confirma persistência no banco via Repository
		Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioSalvo.getId());
		assertTrue(optUsuario.isPresent());
		assertEquals("Joaquim de Souza", optUsuario.get().getNome());
		assertEquals("joaquim@gmail.com", optUsuario.get().getEmail());
		assertEquals("senha123", optUsuario.get().getSenha());
		assertNotNull(optUsuario.get().getDataCadastro());
	}	
	
	@Test
	public void deveLancarExcecaoAoSalvar_QuandoEmailEmUso() {
		Usuario usuario = new Usuario();
		usuario.setNome("João da Silva");
		usuario.setEmail("joao@gmail.com");
		usuario.setSenha("senha123");

		NegocioException ex = assertThrows(NegocioException.class, () -> cadastroUsuario.salvar(usuario));

		assertEquals("Já existe um usuário cadastrado com o e-mail joao@gmail.com", ex.getMessage());
	}	
		
	
	@Test
	public void deveLancarExcecaoAoBuscar_QuandoUsuarioInexistente() {
		UsuarioNaoEncontradoException ex = assertThrows(UsuarioNaoEncontradoException.class, () -> cadastroUsuario.buscarOuFalhar(99L));
		
		assertEquals("Não existe um cadastro de usuário com código 99", ex.getMessage());
	}	
	
	@Test
	public void deveAlterarSenha() {
		Long usuarioId = 1L;
		String senhaAtual = "123";
		String senhaNova = "nova123";
		
		cadastroUsuario.alterarSenha(usuarioId, senhaAtual, senhaNova);
		
		// Confirma persistência no banco via Repository
		Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioId);
		assertTrue(optUsuario.isPresent());
		assertEquals("nova123", optUsuario.get().getSenha());

	}
	
	@Test
	public void deveLancarExcecaoAoAlterarSenha_QuandoSenhaNaoCoincide() {
		Long usuarioId = 1L;
		String senhaAtual = "senhaIncorreta";
		String senhaNova = "nova123";
		
		NegocioException ex = assertThrows(NegocioException.class, () -> cadastroUsuario.alterarSenha(usuarioId, senhaAtual, senhaNova));

		assertEquals("Senha atual informada não coincide com a senha do usuário.", ex.getMessage());
		
		// Confirma 'nao alteracao' no banco via Repository
		Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioId);
		assertTrue(optUsuario.isPresent());
		assertEquals("123", optUsuario.get().getSenha());
	}	
}

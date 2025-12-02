package com.elienai.springfood.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.api.dto.UsuarioRequest;
import com.elienai.springfood.api.dto.UsuarioResponse;
import com.elienai.springfood.api.mapper.UsuarioRequestMapper;
import com.elienai.springfood.api.mapper.UsuarioResponseMapper;
import com.elienai.springfood.domain.model.Usuario;
import com.elienai.springfood.domain.repository.UsuarioRepository;
import com.elienai.springfood.domain.service.CadastroUsuarioService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UsuarioRepository usuarioRepository;
	
	@MockBean
	private CadastroUsuarioService cadastroUsuario;
	
	@MockBean
	private UsuarioResponseMapper usuarioResponseMapper;

	@MockBean
	private UsuarioRequestMapper usuarioRequestMapper;
	
	private Usuario usuarioJose;
	private Usuario usuarioMaria;
	private Usuario usuarioJoao;

	private UsuarioResponse usuarioResponseJose;
	private UsuarioResponse usuarioResponseMaria;
	private UsuarioResponse usuarioResponseJoao;
	
	private String jsonUsuarioJoao;
	private String jsonUsuarioComSenhaJoao;
	private String jsonAlteraSenhaUsuario;
	
	@BeforeEach
	private void setUp() {
		jsonUsuarioJoao = ResourceUtils.getContentFromResource(
				"/json/correto/usuario-joao.json");		
		jsonUsuarioComSenhaJoao = ResourceUtils.getContentFromResource(
				"/json/correto/usuario-com-senha-joao.json");		
		jsonAlteraSenhaUsuario = ResourceUtils.getContentFromResource(
				"/json/correto/usuario-altera-senha.json");
		prepararDados();
	}
	
	@Test
	public void deveListarUsuarios() throws Exception {
		List<Usuario> usuarios = Arrays.asList(usuarioJose, usuarioMaria);
		List<UsuarioResponse> responses = Arrays.asList(usuarioResponseJose, usuarioResponseMaria);
		
		when(usuarioRepository.findAll()).thenReturn(usuarios);
		when(usuarioResponseMapper.toCollectionResponse(usuarios)).thenReturn(responses);
		
		mockMvc.perform(get("/usuarios")
					.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", hasSize(2)))
			   .andExpect(jsonPath("$[0].nome", is("Jose de Souza")))
			   .andExpect(jsonPath("$[1].nome", is("Maria das Dores")));
	}
	
	@Test
	public void deveBuscarUsuarioPorId() throws Exception {
		when(cadastroUsuario.buscarOuFalhar(1L)).thenReturn(usuarioJose);
		when(usuarioResponseMapper.toResponse(usuarioJose)).thenReturn(usuarioResponseJose);
		
		mockMvc.perform(get("/usuarios/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Jose de Souza")));
	}
	
	@Test
	public void deveAdicionarUsuario() throws Exception {
		when(usuarioRequestMapper.toDomainObject(any(UsuarioRequest.class))).thenReturn(usuarioJoao);
		when(cadastroUsuario.salvar(usuarioJoao)).thenReturn(usuarioJoao);
		when(usuarioResponseMapper.toResponse(usuarioJoao)).thenReturn(usuarioResponseJoao);
		
		mockMvc.perform(post("/usuarios")
				.content(jsonUsuarioComSenhaJoao)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nome", is("Joao da Silva")));
	}
	
	@Test
	public void deveAtualizarUsuario() throws Exception {
		when(cadastroUsuario.buscarOuFalhar(1L)).thenReturn(usuarioJoao);
		doNothing().when(usuarioRequestMapper).copyToDomainObject(any(UsuarioRequest.class), any(Usuario.class));
		when(cadastroUsuario.salvar(usuarioJoao)).thenReturn(usuarioJoao);
		when(usuarioResponseMapper.toResponse(usuarioJoao)).thenReturn(usuarioResponseJoao);
		
		mockMvc.perform(put("/usuarios/{id}", 1L)
				.content(jsonUsuarioJoao)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nome", is("Joao da Silva")));
	}

	@Test
	public void deveAlterarSenha() throws Exception {
		when(cadastroUsuario.buscarOuFalhar(1L)).thenReturn(usuarioJose);
		
		mockMvc.perform(put("/usuarios/{id}/senha", 1L)
				.content(jsonAlteraSenhaUsuario)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}
	
	private void prepararDados() {
		usuarioJose = new Usuario();
		usuarioJose.setId(1L);
		usuarioJose.setNome("Jose de Souza");
		usuarioJose.setEmail("jose@gmail.com");
		usuarioJose.setSenha("123");

		usuarioMaria = new Usuario();
		usuarioMaria.setId(2L);
		usuarioMaria.setNome("Maria das Dores");
		
		usuarioJoao = new Usuario();
		usuarioJoao.setId(3L);
		usuarioJoao.setNome("Joao da Silva");
		usuarioJoao.setEmail("joao@gmail.com");
		usuarioJoao.setSenha("123");
		
		
		usuarioResponseJose = new UsuarioResponse();
		usuarioResponseJose.setId(1L);
		usuarioResponseJose.setNome("Jose de Souza");
		usuarioResponseJose.setEmail("jose@gmail.com");
		
		usuarioResponseMaria = new UsuarioResponse();
		usuarioResponseMaria.setId(2L);
		usuarioResponseMaria.setNome("Maria das Dores");
		
		usuarioResponseJoao = new UsuarioResponse();
		usuarioResponseJoao.setId(3L);
		usuarioResponseJoao.setNome("Joao da Silva");
		usuarioResponseJoao.setEmail("joao@gmail.com");
	}
}

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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.elienai.springfood.api.dto.ProdutoRequest;
import com.elienai.springfood.api.dto.ProdutoResponse;
import com.elienai.springfood.api.mapper.ProdutoRequestMapper;
import com.elienai.springfood.api.mapper.ProdutoResponseMapper;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.ProdutoRepository;
import com.elienai.springfood.domain.service.CadastroProdutoService;
import com.elienai.springfood.domain.service.CadastroRestauranteService;
import com.elienai.springfood.util.ResourceUtils;

@WebMvcTest(RestauranteProdutoController.class)
public class RestauranteProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoRepository produtoRepository;

    @MockBean
    private CadastroProdutoService cadastroProduto;

    @MockBean
    private CadastroRestauranteService cadastroRestaurante;

    @MockBean
    private ProdutoResponseMapper produtoResponseMapper;

    @MockBean
    private ProdutoRequestMapper produtoRequestMapper;

    private Restaurante restaurante;
    private Produto produto1;
    private Produto produto2;

    private ProdutoResponse produtoResponse1;
    private ProdutoResponse produtoResponse2;

    private Produto produtoNovo;
    private ProdutoResponse produtoResponseNovo;

    private String jsonProdutoBatata;

    @BeforeEach
    void setUp() {
        jsonProdutoBatata = ResourceUtils.getContentFromResource(
                "/json/correto/produto-batata.json");

        prepararDados();
    }

    @Test
    void deveListarProdutos() throws Exception {
        List<Produto> produtos = Arrays.asList(produto1, produto2);
        List<ProdutoResponse> responses = Arrays.asList(produtoResponse1, produtoResponse2);

        when(cadastroRestaurante.buscarOuFalhar(1L)).thenReturn(restaurante);
        when(produtoRepository.findByRestaurante(restaurante)).thenReturn(produtos);
        when(produtoResponseMapper.toCollectionResponse(produtos)).thenReturn(responses);

        mockMvc.perform(get("/restaurantes/{restauranteId}/produtos", 1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(10)))
            .andExpect(jsonPath("$[0].nome", is("X-Burger")))
            .andExpect(jsonPath("$[0].descricao", is("Hambúrguer artesanal")))
            .andExpect(jsonPath("$[0].preco", is(25.00)))
            .andExpect(jsonPath("$[0].ativo", is(true)))
            .andExpect(jsonPath("$[1].id", is(11)))
            .andExpect(jsonPath("$[1].nome", is("Refrigerante")))
            .andExpect(jsonPath("$[1].descricao", is("Coca-cola lata")))
            .andExpect(jsonPath("$[1].preco", is(6.00)))
            .andExpect(jsonPath("$[1].ativo", is(true)));
    }

    @Test
    void deveBuscarProduto() throws Exception {
        when(cadastroProduto.buscarOuFalhar(1L, 10L)).thenReturn(produto1);
        when(produtoResponseMapper.toResponse(produto1)).thenReturn(produtoResponse1);

        mockMvc.perform(get("/restaurantes/{restauranteId}/produtos/{produtoId}", 1L, 10L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(10)))
            .andExpect(jsonPath("$.nome", is("X-Burger")))
            .andExpect(jsonPath("$.descricao", is("Hambúrguer artesanal")))
            .andExpect(jsonPath("$.preco", is(25.00)))
            .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    void deveAdicionarProduto() throws Exception {
        when(cadastroRestaurante.buscarOuFalhar(1L)).thenReturn(restaurante);
        when(produtoRequestMapper.toDomainObject(any(ProdutoRequest.class))).thenReturn(produtoNovo);
        when(cadastroProduto.salvar(produtoNovo)).thenReturn(produtoNovo);
        when(produtoResponseMapper.toResponse(produtoNovo)).thenReturn(produtoResponseNovo);

        mockMvc.perform(post("/restaurantes/{restauranteId}/produtos", 1L)
                .content(jsonProdutoBatata)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome", is("Batata Frita")))
            .andExpect(jsonPath("$.descricao", is("Porção média")))
            .andExpect(jsonPath("$.preco", is(12.50)))
            .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        when(cadastroProduto.buscarOuFalhar(1L, 10L)).thenReturn(produto1);
        doNothing().when(produtoRequestMapper).copyToDomainObject(any(ProdutoRequest.class), any(Produto.class));
        when(cadastroProduto.salvar(produto1)).thenReturn(produtoNovo);
        when(produtoResponseMapper.toResponse(produtoNovo)).thenReturn(produtoResponseNovo);

        mockMvc.perform(put("/restaurantes/{restauranteId}/produtos/{produtoId}", 1L, 10L)
                .content(jsonProdutoBatata)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Batata Frita")))
            .andExpect(jsonPath("$.descricao", is("Porção média")))
            .andExpect(jsonPath("$.preco", is(12.50)))
            .andExpect(jsonPath("$.ativo", is(true)));
    }

    // ----------------------------------------------------------------------
    // Dados de teste
    // ----------------------------------------------------------------------
    private void prepararDados() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Lanchonete");

        produto1 = new Produto();
        produto1.setId(10L);
        produto1.setNome("X-Burger");
        produto1.setDescricao("Hambúrguer artesanal");
        produto1.setPreco(BigDecimal.valueOf(25.00));
        produto1.setAtivo(true);
        produto1.setRestaurante(restaurante);

        produto2 = new Produto();
        produto2.setId(11L);
        produto2.setNome("Refrigerante");
        produto2.setDescricao("Coca-cola lata");
        produto2.setPreco(BigDecimal.valueOf(6.00));
        produto2.setAtivo(true);
        produto2.setRestaurante(restaurante);

        produtoResponse1 = new ProdutoResponse();
        produtoResponse1.setId(10L);
        produtoResponse1.setNome("X-Burger");
        produtoResponse1.setDescricao("Hambúrguer artesanal");
        produtoResponse1.setPreco(BigDecimal.valueOf(25.00));
        produtoResponse1.setAtivo(true);

        produtoResponse2 = new ProdutoResponse();
        produtoResponse2.setId(11L);
        produtoResponse2.setNome("Refrigerante");
        produtoResponse2.setDescricao("Coca-cola lata");
        produtoResponse2.setPreco(BigDecimal.valueOf(6.00));
        produtoResponse2.setAtivo(true);

        // Produto novo
        produtoNovo = new Produto();
        produtoNovo.setId(99L);
        produtoNovo.setNome("Batata Frita");
        produtoNovo.setDescricao("Porção média");
        produtoNovo.setPreco(BigDecimal.valueOf(12.50));
        produtoNovo.setAtivo(true);
        produtoNovo.setRestaurante(restaurante);

        produtoResponseNovo = new ProdutoResponse();
        produtoResponseNovo.setId(99L);
        produtoResponseNovo.setNome("Batata Frita");
        produtoResponseNovo.setDescricao("Porção média");
        produtoResponseNovo.setPreco(BigDecimal.valueOf(12.50));
        produtoResponseNovo.setAtivo(true);
    }
}


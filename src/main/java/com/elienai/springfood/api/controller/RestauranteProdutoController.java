package com.elienai.springfood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.elienai.springfood.api.dto.ProdutoRequest;
import com.elienai.springfood.api.dto.ProdutoResponse;
import com.elienai.springfood.api.mapper.ProdutoRequestMapper;
import com.elienai.springfood.api.mapper.ProdutoResponseMapper;
import com.elienai.springfood.domain.model.Produto;
import com.elienai.springfood.domain.model.Restaurante;
import com.elienai.springfood.domain.repository.ProdutoRepository;
import com.elienai.springfood.domain.service.CadastroProdutoService;
import com.elienai.springfood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/produtos")
public class RestauranteProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private CadastroProdutoService cadastroProduto;
    
    @Autowired
    private CadastroRestauranteService cadastroRestaurante;
    
    @Autowired
    private ProdutoResponseMapper produtoResponseMapper;
    
    @Autowired
    private ProdutoRequestMapper produtoRequestMapper;
    
    @GetMapping
    public List<ProdutoResponse> listar(@PathVariable Long restauranteId) {
        Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(restauranteId);
        
        List<Produto> todosProdutos = produtoRepository.findByRestaurante(restaurante);
        
        return produtoResponseMapper.toCollectionResponse(todosProdutos);
    }
    
    @GetMapping("/{produtoId}")
    public ProdutoResponse buscar(@PathVariable Long restauranteId, @PathVariable Long produtoId) {
        Produto produto = cadastroProduto.buscarOuFalhar(restauranteId, produtoId);
        
        return produtoResponseMapper.toResponse(produto);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse adicionar(@PathVariable Long restauranteId,
            @RequestBody @Valid ProdutoRequest produtoRequest) {
        Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(restauranteId);
        
        Produto produto = produtoRequestMapper.toDomainObject(produtoRequest);
        produto.setRestaurante(restaurante);
        
        produto = cadastroProduto.salvar(produto);
        
        return produtoResponseMapper.toResponse(produto);
    }
    
    @PutMapping("/{produtoId}")
    public ProdutoResponse atualizar(@PathVariable Long restauranteId, @PathVariable Long produtoId,
            @RequestBody @Valid ProdutoRequest produtoRequest) {
        Produto produtoAtual = cadastroProduto.buscarOuFalhar(restauranteId, produtoId);
        
        produtoRequestMapper.copyToDomainObject(produtoRequest, produtoAtual);
        
        produtoAtual = cadastroProduto.salvar(produtoAtual);
        
        return produtoResponseMapper.toResponse(produtoAtual);
    }   
}   

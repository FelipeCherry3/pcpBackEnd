package com.rubim.pcpBackEnd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rubim.pcpBackEnd.Services.BlingPedidoVendaService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "produtos")
public class BlingProdutosController {

    private final BlingPedidoVendaService blingProdutoService;

    @Autowired
    public BlingProdutosController(BlingPedidoVendaService blingProdutoService) {
        this.blingProdutoService = blingProdutoService;
    }

    @RequestMapping("/getProdutos")
    public Mono<String> getProdutos() {
        return Mono.just("Produtos Sincronizados :" + blingProdutoService.sincronizarProdutos());
    }

}

package com.rubim.pcpBackEnd.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.rubim.pcpBackEnd.Services.BlingPedidoVendaService;
import com.rubim.pcpBackEnd.Services.UserFrontService;

import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(path = "pedidos")
public class PedidoVendaController {

    private final BlingPedidoVendaService blingPedidoVendaService;
    private final UserFrontService userService;

    public PedidoVendaController(BlingPedidoVendaService blingPedidoVendaService, UserFrontService userService) {
        this.blingPedidoVendaService = blingPedidoVendaService;
        this.userService = userService;
    }

    @GetMapping("/getVendas")
    public Mono<String> getPedidosVenda(@RequestParam String dataInicial,
                                        @RequestParam String dataFinal,
                                        @RequestParam String password) {
        if (!userService.validateSincronizarBling(password)) {
            throw new IllegalArgumentException("Senha inválida");
        } else {
        LocalDate di = com.rubim.pcpBackEnd.utils.JsonParserUtil.toLocalDate(dataInicial);
        LocalDate df = com.rubim.pcpBackEnd.utils.JsonParserUtil.toLocalDate(dataFinal);
        int response = blingPedidoVendaService.sincronizarPedidosDoPeriodo(di, df);
        return Mono.just("Número de pedidos sincronizados: " + response);
        }
    }
}
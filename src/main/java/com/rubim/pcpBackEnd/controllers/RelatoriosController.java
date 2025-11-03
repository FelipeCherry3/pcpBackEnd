package com.rubim.pcpBackEnd.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rubim.pcpBackEnd.DTO.RelatorioPedidosEntreguesResponseDTO;
import com.rubim.pcpBackEnd.Services.RelatoriosService;
import com.rubim.pcpBackEnd.Services.UserFrontService;

@RestController
@RequestMapping("/api/relatorios")
public class RelatoriosController {
    
    private final RelatoriosService service;
    private final UserFrontService userFrontService;

    public RelatoriosController(RelatoriosService service, UserFrontService userFrontService) {
        this.service = service;
        this.userFrontService = userFrontService;
    }

    // GET /relatorios/pedidos-entregues?dataInicial=2025-10-01&dataFinal=2025-10-31
    @GetMapping("/pedidos-entregues")
    public RelatorioPedidosEntreguesResponseDTO pedidosEntregues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam String password
    ) {
        if (!userFrontService.validateCarregarDados(password)) {
            throw new IllegalArgumentException("Senha inválida");
        }
        return service.relatorioPedidosEntregues(dataInicial, dataFinal);
    }
}

package com.rubim.pcpBackEnd.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rubim.pcpBackEnd.DTO.RelatorioPedidosEntreguesResponseDTO;
import com.rubim.pcpBackEnd.Services.RelatoriosService;

@RestController
@RequestMapping("/api/relatorios")
public class RelatoriosController {
    
    private final RelatoriosService service;

    public RelatoriosController(RelatoriosService service) {
        this.service = service;
    }

    // GET /relatorios/pedidos-entregues?dataInicial=2025-10-01&dataFinal=2025-10-31
    @GetMapping("/pedidos-entregues")
    public RelatorioPedidosEntreguesResponseDTO pedidosEntregues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return service.relatorioPedidosEntregues(dataInicial, dataFinal);
    }
}

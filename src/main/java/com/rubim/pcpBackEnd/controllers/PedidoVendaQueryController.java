package com.rubim.pcpBackEnd.controllers;

import com.rubim.pcpBackEnd.DTO.PedidoVendaResponseDTO;
import com.rubim.pcpBackEnd.Services.PedidoQueryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/pedidos-venda")
public class PedidoVendaQueryController {

    private final PedidoQueryService service;

    public PedidoVendaQueryController(PedidoQueryService service) {
        this.service = service;
    }

    // GET por período: /api/pedidos-venda?dataInicial=2025-08-21&dataFinal=2025-08-28
    @GetMapping
    public List<PedidoVendaResponseDTO> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("dataFinal não pode ser anterior a dataInicial");
        }
        return service.listarPorPeriodo(dataInicial, dataFinal);
    }

    // GET por id: /api/pedidos-venda/123456
    @GetMapping("/{id}")
    public PedidoVendaResponseDTO detalhar(@PathVariable Long id) {
        return service.detalhar(id);
    }

    @PutMapping("/atualizarSetor")
    public String atualizarSetor(@RequestBody Long idPedido, @RequestBody Long idSetor) {
        service.atualizarSetorDePedido(idPedido, idSetor);
        return "Setor do pedido " + idPedido + " atualizado para o setor " + idSetor + " com sucesso";
    }
    
}
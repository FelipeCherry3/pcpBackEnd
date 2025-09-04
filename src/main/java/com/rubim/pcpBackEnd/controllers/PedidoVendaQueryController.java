package com.rubim.pcpBackEnd.controllers;

import com.rubim.pcpBackEnd.DTO.AtualizarSetorDTO;
import com.rubim.pcpBackEnd.DTO.PedidoVendaResponseDTO;
import com.rubim.pcpBackEnd.Services.PedidoQueryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<?> atualizarSetor(@RequestBody AtualizarSetorDTO dto) {
        if (dto.getIdPedido() == null || dto.getIdNovoSetor() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "IDs do pedido e do setor são obrigatórios"));
        }

        boolean ok = service.atualizarSetorDePedido(dto.getIdPedido(), dto.getIdNovoSetor());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pedido ou Setor não encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Setor atualizado",
                "idPedido", dto.getIdPedido(),
                "idNovoSetor", dto.getIdNovoSetor()
        ));
    }
    
}
package com.rubim.pcpBackEnd.controllers;

import com.rubim.pcpBackEnd.DTO.AtualizarSetorDTO;
import com.rubim.pcpBackEnd.DTO.BaixaPedidoDTO;
import com.rubim.pcpBackEnd.DTO.PedidoVendaResponseDTO;
import com.rubim.pcpBackEnd.Services.PedidoQueryService;
import com.rubim.pcpBackEnd.Services.UserFrontService;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
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

    private final UserFrontService userService;

    public PedidoVendaQueryController(PedidoQueryService service, UserFrontService userService) {
        this.service = service;
        this.userService = userService;
    }

    // GET por período: /api/pedidos-venda?dataInicial=2025-08-21&dataFinal=2025-08-28
    @GetMapping
    public List<PedidoVendaResponseDTO> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam String password) {
        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("dataFinal não pode ser anterior a dataInicial");
        }

        if (!userService.validateCarregarDados(password)) {
            throw new IllegalArgumentException("Senha inválida");
        }

        return service.listarPorPeriodo(dataInicial, dataFinal);
    }

    // GET por id: /api/pedidos-venda/123456
    @GetMapping("/{id}")
    public PedidoVendaResponseDTO detalhar(@PathVariable Long id) {
        return service.detalhar(id);
    }

    @PutMapping("/atualizarDados")
    public ResponseEntity<?> atualizarDados(@RequestBody PedidoVendaResponseDTO dto) {
        if (dto.getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ID do pedido é obrigatório"));
        }

        boolean ok = service.atualizarDadosPedido(dto);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pedido não encontrado"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Dados atualizados",
                "idPedido", dto.getId()
        ));
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

    @PutMapping("/marcarComoEntregue")
    public ResponseEntity<?> marcarComoEntregue(@RequestBody BaixaPedidoDTO dto) {
        if (!userService.validateCarregarDados(dto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Senha inválida"));
        }
        String resultado = service.atualizarPedidosEntreguesPorNumero(dto.getNumerosPedidos());
        if (resultado != null) {
            return ResponseEntity.ok(Map.of("message", resultado));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Nenhum pedido encontrado"));
    }

}
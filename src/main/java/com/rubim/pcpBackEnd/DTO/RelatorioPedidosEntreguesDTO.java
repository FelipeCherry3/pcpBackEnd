package com.rubim.pcpBackEnd.DTO;

import java.time.LocalDateTime;

import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;

import lombok.Data;

@Data
public class RelatorioPedidosEntreguesDTO {
    private Long idMovimentacao;
    private String descricao;
    private int numeroPedido;
    private int quantidadePecas;
    private int totalPecas;
    private LocalDateTime dataEntrega;
}

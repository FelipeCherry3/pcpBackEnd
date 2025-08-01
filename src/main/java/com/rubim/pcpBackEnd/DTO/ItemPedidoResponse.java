package com.rubim.pcpBackEnd.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedidoResponse {

    private Long id;
    private String codigo;
    private String unidade;
    private Integer desconto;
    private Integer valor;
    private String descricao;
    private Integer quantidade;
    private String descricaoDetalhada;
    private Produto produto;
    private ClienteDTOResponse cliente;
    private PedidoVendaResponse pedidoVenda;
}

package com.rubim.pcpBackEnd.DTO;

import com.rubim.pcpBackEnd.Entity.ProdutoEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedidoResponseDTO {

    private Long id;
    private String codigo;
    private String unidade;
    private Integer desconto;
    private Integer valor;
    private String descricao;
    private Integer quantidade;
    private String descricaoDetalhada;
    private String corMadeira;
    private String corRevestimento;
    private String detalhesMedidas;
    private ProdutoDTO produto;
    private PedidoVendaResponseDTO pedidoVenda;


    public void setProduto(ProdutoDTO produto) {
        this.produto = produto;
    }
}

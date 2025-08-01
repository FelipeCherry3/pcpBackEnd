package com.rubim.pcpBackEnd.DTO;
import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoVendaResponse {
    private Long id;
    private Long numero;
    private Date dataEmissao;
    private Date dataEntrega;
    private Date dataPrevista;
    private Integer total;
    private String cliente;
    private List<ItemPedidoResponse> itens;

    // Getters and Setters
}

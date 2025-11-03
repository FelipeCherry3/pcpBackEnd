package com.rubim.pcpBackEnd.DTO;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioPedidosEntreguesRowDTO {
    private String numeroPedido;
    private String nomeCliente;
    private OffsetDateTime dataPedido;
    private OffsetDateTime dataEntrega;
    private Long quantidadePecas;
    private Long leadTimeDias;

}

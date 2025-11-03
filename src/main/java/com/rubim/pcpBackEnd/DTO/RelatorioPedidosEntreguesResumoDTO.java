package com.rubim.pcpBackEnd.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioPedidosEntreguesResumoDTO {

    private Long totalPedidosEntregues;
    private Long totalPecasEntregues;
    private BigDecimal tempoMedioProducaoDias;
}

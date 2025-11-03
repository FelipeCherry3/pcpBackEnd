package com.rubim.pcpBackEnd.DTO;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioPedidosEntreguesResponseDTO {
    private LocalDate periodoInicial;
    private LocalDate periodoFinal;
    private List<RelatorioPedidosEntreguesRowDTO> itens;
    private RelatorioPedidosEntreguesResumoDTO resumo;
}

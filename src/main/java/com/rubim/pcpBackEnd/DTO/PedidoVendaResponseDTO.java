package com.rubim.pcpBackEnd.DTO;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.rubim.pcpBackEnd.Entity.SetorEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoVendaResponseDTO {
    private Long id;
    private Long numero;
    private LocalDate dataEmissao;
    private LocalDate dataEntrega;
    private LocalDate dataPrevista;
    private Integer total;
    private SetorDTO setor;
    private ClienteDTOResponse cliente;
    private List<ItemPedidoResponseDTO> itens;
    private String priority;
}

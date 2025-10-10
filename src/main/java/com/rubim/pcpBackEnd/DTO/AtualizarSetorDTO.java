package com.rubim.pcpBackEnd.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarSetorDTO {
    private Long idNovoSetor;
    private Long idPedido;
    private String password;
}

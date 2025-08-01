package com.rubim.pcpBackEnd.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTOResponse {
    private Long id ;
    private String nome;
    private Integer tipoPessoa;

}

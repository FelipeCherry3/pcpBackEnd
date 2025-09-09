package com.rubim.pcpBackEnd.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "setor")
public class SetorEntity {
    @Id
    private Long id;
    private String nome;
    
}

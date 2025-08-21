package com.rubim.pcpBackEnd.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "situacao")
@Data
@Entity
public class SituacaoEntity {
    @Id
    private Long id;
    private String valor;
}

package com.rubim.pcpBackEnd.Entity;

import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "situacao")
@Data
public class SituacaoEntity {
    private String id;
    private String valor;
}

package com.rubim.pcpBackEnd.Entity;

import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "contato")
@Data
public class ContatoEntity {
    private Long id;
	private String nome;
	private String tipoPessoa;
	private String numeroDocumento;
}

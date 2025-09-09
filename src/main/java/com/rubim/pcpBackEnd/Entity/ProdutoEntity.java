package com.rubim.pcpBackEnd.Entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoEntity {

	@Id
    private Long id;
	private String nome;
	private String codigo;
    private String tipo;
	private BigDecimal preco;
	private BigDecimal precoCusto;
	private String situacao;
	private String descricaoCurta;

	@OneToMany(mappedBy = "produto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private List<EstoqueEntity> estoques = new ArrayList<>();
}

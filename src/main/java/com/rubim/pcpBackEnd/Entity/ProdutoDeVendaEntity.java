package com.rubim.pcpBackEnd.Entity;

import java.math.BigDecimal;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rubim.pcpBackEnd.DTO.Produto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "itens_venda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDeVendaEntity {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidosVendaEntity pedidosVenda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @Column(length = 100)
	private String codigo;
	private String unidade;
    
    @Column(precision = 10, scale = 2)
	private BigDecimal quantidade;
    
    @Column(precision = 10, scale = 2)
	private BigDecimal desconto;
    
    @Column(precision = 10, scale = 2)
	private BigDecimal valor;
    private String corMadeira;
    private String corRevestimento;
    private String medidasTampo;
    
	@Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition ="TEXT")
	private String descricaoDetalhada;
}

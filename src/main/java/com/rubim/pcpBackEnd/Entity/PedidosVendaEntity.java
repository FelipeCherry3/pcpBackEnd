package com.rubim.pcpBackEnd.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.Index;

@Entity

@Table(
  name = "pedidos_venda",
  indexes = {
    @Index(name = "idx_pedido_vendedor", columnList = "vendedor_id"),
    @Index(name = "idx_pedido_contato",  columnList = "contato_id"),
    @Index(name = "idx_pedido_situacao", columnList = "situacao_id")
  }
)
@Data
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class PedidosVendaEntity {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
	private Long numero;
	private LocalDate data;
	private LocalDate dataSaida;
	private LocalDate dataPrevista;

    @Column(name = "priority", length = 50)
    private String priority;

    @Column(precision = 18, scale = 2)
	private BigDecimal totalProdutos;

    @Column(precision = 18, scale = 2)
	private BigDecimal total;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contato_id", nullable = false)
    @ToString.Exclude
    private ContatoEntity contato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "situacao_id", nullable = false)
    @ToString.Exclude
	private SituacaoEntity situacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    @ToString.Exclude
    private VendedorEntity vendedor;

    @Column(columnDefinition = "TEXT")
	private String observacoes;
	
    @Column(columnDefinition = "TEXT")
    private String observacoesInternas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private SetorEntity setor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
	private List<ProdutoDeVendaEntity> itens = new ArrayList<>();
	
}

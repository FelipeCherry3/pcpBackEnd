package com.rubim.pcpBackEnd.Entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Data;

@Data
@Entity
@Table(name = "movimentos_estoque", indexes = {
    @Index(name = "idx_mov_estoque_id", columnList = "estoque_id"),
    @Index(name = "idx_mov_data", columnList = "criadoEm")
})
public class EstoqueMovimentacaoEntity {

@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="estoque_id", nullable=false)
    private EstoqueEntity estoque;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private TipoMovimento tipo; // ENTRADA, SAIDA, AJUSTE

    @Column(nullable=false)
    private Integer quantidade; // positiva

    @Column(length=180)
    private String motivo; // ex.: "NF 12345", "Pedido 9001", "Inventário"

    @Column(length=80)
    private String referencia; // id externo (pedido, NF, etc.)-

    @Column(nullable=false)
    private Instant criadoEm = Instant.now();

    public enum TipoMovimento { ENTRADA, SAIDA, AJUSTE }
}

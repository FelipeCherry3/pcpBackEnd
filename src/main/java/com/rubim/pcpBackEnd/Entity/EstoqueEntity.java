package com.rubim.pcpBackEnd.Entity;

import org.hibernate.engine.internal.ForeignKeys;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;


@Data
@Entity
@Table(name = "estoques",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_produto_deposito", columnNames = {"produto_id","deposito"})
       },
       indexes = {
           @Index(name = "idx_produto_id", columnList = "produto_id"),
           @Index(name = "idx_deposito", columnList = "deposito")
       })
public class EstoqueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deposito;
    private Integer minimo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @Column(name = "quantidade")
    private Integer saldoVirtualTotal;
    
    @Version
    @Column(name="version")
    private Long version;

}

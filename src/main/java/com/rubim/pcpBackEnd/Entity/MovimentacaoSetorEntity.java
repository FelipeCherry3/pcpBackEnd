package com.rubim.pcpBackEnd.Entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.rubim.pcpBackEnd.Entity.user.UserFrontEntity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "movimento_setor",
    indexes = {
        @Index(name = "idx_mov_setor_pedido", columnList = "id_pedido"),
        @Index(name = "idx_mov_setor_user", columnList = "nome_user"),
        @Index(name = "idx_mov_setor_criado_em", columnList = "criado_em")
    }
)
@Data
public class MovimentacaoSetorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mov_pedido"))
    private PedidosVendaEntity pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user",
            foreignKey = @ForeignKey(name = "fk_mov_user"))
    private UserFrontEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_setor_antigo",
            foreignKey = @ForeignKey(name = "fk_mov_setor_antigo"))
    private SetorEntity setorAntigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_setor_atual", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mov_setor_atual"))
    private SetorEntity setorAtual;

    @Column(name = "descricao", nullable = false)
    private String descricao; // RECUO_PRODUCAO ou AVANCO_PRODUCAO

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;
    
}

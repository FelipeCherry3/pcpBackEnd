package com.rubim.pcpBackEnd.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;

public interface RelatorioRepository extends JpaRepository<PedidosVendaEntity, Long> {
        /**
     * Retorna uma linha por pedido entregue no período.
     * - Considera apenas a ÚLTIMA baixa (setor 7) de cada pedido dentro do intervalo.
     * - Soma total de peças por pedido (itens_venda).
     *
     * Colunas retornadas (em ordem):
     *  0: pedido_id (Long)
     *  1: numero (String)
     *  2: nome_cliente (String)
     *  3: data (OffsetDateTime)
     *  4: data_entrega (OffsetDateTime)
     *  5: total_pecas (Long)
     */
    @Query(value = """
    WITH entregas AS (
        SELECT DISTINCT ON (m.id_pedido)
               m.id_pedido,
               m.criado_em AS data_entrega
        FROM public.movimento_setor m
        WHERE m.id_setor_atual = 7
          AND m.criado_em >= :inicio
          AND m.criado_em <  :fim
        ORDER BY m.id_pedido, m.criado_em DESC
    )
    SELECT 
        p.id                                    AS pedido_id,
        p.numero                                AS numero,
        c.nome                                   AS nome_cliente,
        p.data                                  AS data_pedido,
        e.data_entrega                          AS data_entrega,
        SUM(iv.quantidade)::bigint              AS total_pecas
    FROM entregas e
    JOIN public.pedidos_venda p   ON p.id = e.id_pedido
    LEFT JOIN public.contato c     ON c.id = p.contato_id
    JOIN public.itens_venda iv     ON iv.pedido_id = p.id
    GROUP BY p.id, p.numero, c.nome, p.data, e.data_entrega
    ORDER BY e.data_entrega ASC
    """, nativeQuery = true)
    List<Object[]> listarPedidosEntreguesNoPeriodo(
            @Param("inicio") OffsetDateTime inicio,
            @Param("fim")    OffsetDateTime fim
    );
}

package com.rubim.pcpBackEnd.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;
import com.rubim.pcpBackEnd.Entity.ProdutoEntity;

public interface PedidosVendaRepository extends JpaRepository<PedidosVendaEntity, Long> {
    // Custom query methods (if needed) can be defined here
    List<PedidosVendaEntity> findAllByDataBetween(LocalDate dataInicial, LocalDate dataFinal);

    @Query("select p.id from PedidosVendaEntity p where p.id in :ids")
    List<Long> findExistingIds(@Param("ids") Collection<Long> ids);
}

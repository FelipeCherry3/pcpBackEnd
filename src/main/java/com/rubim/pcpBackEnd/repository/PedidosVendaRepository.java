package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.PedidosVendaEntity;

public interface PedidosVendaRepository extends JpaRepository<PedidosVendaEntity, Long> {
    // Custom query methods (if needed) can be defined here
}

package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.ProdutoDeVendaEntity;

public interface ProdutoVendaRepository extends JpaRepository<ProdutoDeVendaEntity, Long> {
    // Custom query methods (if needed) can be defined here
}

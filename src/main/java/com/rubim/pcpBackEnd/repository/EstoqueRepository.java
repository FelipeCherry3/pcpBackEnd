package com.rubim.pcpBackEnd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.EstoqueEntity;
import com.rubim.pcpBackEnd.Entity.ProdutoEntity;

public interface EstoqueRepository extends JpaRepository<EstoqueEntity, Long> {
    Optional<EstoqueEntity> findByProdutoAndDeposito(ProdutoEntity produto, String deposito);
}

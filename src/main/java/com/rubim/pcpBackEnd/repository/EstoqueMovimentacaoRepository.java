package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.EstoqueMovimentacaoEntity;

public interface EstoqueMovimentacaoRepository extends JpaRepository<EstoqueMovimentacaoEntity, Long> {
}

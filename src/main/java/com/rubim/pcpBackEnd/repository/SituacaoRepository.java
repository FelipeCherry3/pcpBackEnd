package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.SituacaoEntity;

public interface SituacaoRepository extends JpaRepository<SituacaoEntity, Long> {
    // Custom query methods (if needed) can be defined here
}

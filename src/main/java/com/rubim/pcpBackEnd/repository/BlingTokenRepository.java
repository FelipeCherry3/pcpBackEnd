package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rubim.pcpBackEnd.Entity.BlingTokenEntity;

@Repository
public interface BlingTokenRepository extends JpaRepository<BlingTokenEntity, Long> {
    BlingTokenEntity findTopByOrderByCreatedAtDesc();
}
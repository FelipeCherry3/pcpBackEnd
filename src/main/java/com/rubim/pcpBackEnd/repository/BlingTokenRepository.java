package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rubim.pcpBackEnd.Entity.BlingTokenEntity;

@Repository
public interface BlingTokenRepository extends JpaRepository<BlingTokenEntity, Long> {
    BlingTokenEntity findTopByOrderByCreatedAtDesc();
    BlingTokenEntity findByAccessToken(String accessToken);
    BlingTokenEntity findByRefreshToken(String refreshToken);
    void deleteByAccessToken(String accessToken);
    void deleteByRefreshToken(String refreshToken);
    BlingTokenEntity save(BlingTokenEntity token);
    void deleteById(Long id);
}
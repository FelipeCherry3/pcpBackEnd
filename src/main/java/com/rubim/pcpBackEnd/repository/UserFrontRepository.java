package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.UserFrontEntity;

public interface UserFrontRepository extends JpaRepository<UserFrontEntity, String> {
    UserFrontEntity findByUsernameString(String username);
    UserFrontEntity findByPasswordString(String password);
}

package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.rubim.pcpBackEnd.Entity.user.UserFrontEntity;

public interface UserFrontRepository extends JpaRepository<UserFrontEntity, String> {
    UserFrontEntity findByUsernameString(String username);
    UserFrontEntity findByPasswordString(String password);
    UserDetails findByLogin(String login);
}

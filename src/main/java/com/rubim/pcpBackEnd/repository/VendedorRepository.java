package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rubim.pcpBackEnd.Entity.VendedorEntity;

public interface VendedorRepository extends JpaRepository<VendedorEntity, Long> {
    // Custom query methods (if needed) can be defined here
}

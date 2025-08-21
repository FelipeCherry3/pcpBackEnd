package com.rubim.pcpBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rubim.pcpBackEnd.Entity.SetorEntity;

@Repository
public interface SetorRepository extends JpaRepository<SetorEntity, Long> {

}

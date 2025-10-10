package com.rubim.pcpBackEnd.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserFrontEntity {
    
    @Column(name = "nome")
    @Id
    private String usernameString;

    @Column(name = "senha")
    private String passwordString;
}

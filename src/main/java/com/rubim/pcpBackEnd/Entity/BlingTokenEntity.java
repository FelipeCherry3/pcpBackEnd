package com.rubim.pcpBackEnd.Entity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.persistence.Table;


@Entity
@Table(name = "bling_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlingTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;

    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
}

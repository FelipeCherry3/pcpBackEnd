package com.rubim.pcpBackEnd.Services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.rubim.pcpBackEnd.Entity.user.UserFrontEntity;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserFrontEntity user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withSubject(user.getLogin())
                .withIssuer("pcp-backend")
                .withExpiresAt(genExpirationDate())
                .sign(algorithm);
            return token;
        } catch(JWTCreationException e){
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }

    }

    public String validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return  JWT.require(algorithm)
                .withIssuer("pcp-backend")
                .build()
                .verify(token)
                .getSubject();
        } catch(Exception e){
            throw new RuntimeException("Token JWT inválido", e);
        }
    }
    
    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
    
}

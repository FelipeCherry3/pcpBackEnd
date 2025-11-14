package com.rubim.pcpBackEnd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rubim.pcpBackEnd.Entity.user.UserFrontEntity;
import com.rubim.pcpBackEnd.repository.UserFrontRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserFrontRepository repository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthDTO data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody RegisterDTO data) {
        if(this.repository.findByLogin(data.login()) != null || this.repository.findByUsernameString(data.usernameString()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.passwordString());
        var user = new UserFrontEntity(data.usernameString(), encryptedPassword, data.role(), data.login());
        this.repository.save(user);
        return ResponseEntity.ok().build();
    }
}

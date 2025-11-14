package com.rubim.pcpBackEnd.Services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rubim.pcpBackEnd.repository.UserFrontRepository;

@Service
public class AuthorizationService implements UserDetailsService{

    @Autowired
    UserFrontRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
    
}

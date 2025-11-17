package com.rubim.pcpBackEnd.configuration;

import java.io.IOException;
import java.security.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rubim.pcpBackEnd.Services.JwtService;
import com.rubim.pcpBackEnd.repository.UserFrontRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter{

    @Autowired
    JwtService jwtService;

    @Autowired
    UserFrontRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null){
            var login = jwtService.validateToken(token);
            UserDetails userDetails = userRepository.findByLogin(login);

            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        }
        filterChain.doFilter(request, response);
    }
    
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null ) return null; 
        return authHeader.replace("Bearer ", "");
    }

}
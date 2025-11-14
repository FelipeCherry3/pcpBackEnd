package com.rubim.pcpBackEnd.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rubim.pcpBackEnd.Entity.user.UserFrontEntity;
import com.rubim.pcpBackEnd.repository.UserFrontRepository;

@Service
public class UserFrontService {

    @Autowired
    private UserFrontRepository userFrontRepository;

    public UserFrontEntity findByUsername(String username) {
        return userFrontRepository.findByUsernameString(username);
    }

    public UserFrontEntity findByPassword(String password) {
        return userFrontRepository.findByPasswordString(password);
    }

    public Boolean validateMudancaSetor(String password) {
        List<UserFrontEntity> users = userFrontRepository.findAll();
        for (UserFrontEntity u : users) {
            if (u.getPasswordString().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Boolean validateCarregarDados(String password) {
        List<UserFrontEntity> users = userFrontRepository.findAll();
        for (UserFrontEntity u : users) {
            if (u.getPasswordString().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Boolean validateSincronizarBling(String password) {
        UserFrontEntity admin = userFrontRepository.findByUsernameString("Felipe");
        if (admin != null && admin.getPasswordString().equals(password)) {
            return true;
        }
        return false;
    }

}

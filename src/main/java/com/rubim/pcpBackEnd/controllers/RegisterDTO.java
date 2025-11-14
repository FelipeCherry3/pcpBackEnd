package com.rubim.pcpBackEnd.controllers;

import com.rubim.pcpBackEnd.Entity.user.UserRole;

public record RegisterDTO(String usernameString, String login, String passwordString, UserRole role) {

}

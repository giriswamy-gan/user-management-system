package com.apica.user_service.controller;

import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.Users;
import com.apica.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping
//    public List<Users> getUsers() {
//
//    }
}

package com.apica.user_service.service;

import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    UserRepository userRepository;

    RolesRepository rolesRepository;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

//    public void registerUser(UserRequestDto userReq) {
//        Users users = new Users();
//        if (rolesRepository.findById(userReq.getRoleId()).isEmpty()) {
//
//        }
//        if (Objects.nonNull(userReq.getPassword()) && Objects.nonNull(userRepository.findByUsername(userReq.getUsername()))) {
//            throw new RuntimeException("Username already exists");
//        }
//        users.setUsername(userReq.getUsername());
//        users.setPassword(userReq.getPassword());
//        users.setFullName(userReq.getFullName());
//
//    }

}

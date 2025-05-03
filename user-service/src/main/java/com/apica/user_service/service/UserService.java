package com.apica.user_service.service;

import com.apica.user_service.dto.GetUserResponseDto;
import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.utils.CustomApiException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    UserRepository userRepository;
    RolesRepository rolesRepository;
    PasswordEncoder pwEncoder;
    KafkaService kafkaService;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder pwEncoder, KafkaService kafkaService) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.pwEncoder = pwEncoder;
        this.kafkaService = kafkaService;
    }

    public List<GetUserResponseDto> getAllUsers() {
        List<Users> users = userRepository.findAll();
        List<GetUserResponseDto> usersDto = new ArrayList<>();
        for (Users user : users) {
            GetUserResponseDto userDto = new GetUserResponseDto();
            userDto.setUserId(user.getId());
            userDto.setUserName(user.getUsername());
            userDto.setFullName(user.getFullName());
            usersDto.add(userDto);
        }
        return usersDto;
    }

    public GetUserResponseDto getUser(String userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("User not found", HttpStatus.BAD_REQUEST, "USER_NOT_FOUND"));
        GetUserResponseDto userDto = new GetUserResponseDto();
        userDto.setUserId(user.getId());
        userDto.setUserName(user.getUsername());
        userDto.setFullName(user.getFullName());
        return userDto;
    }

    @Transactional
    public void updateUser(String userId, UserRequestDto userRequestDto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("User not found", HttpStatus.BAD_REQUEST, "USER_NOT_FOUND"));
        user.setFullName(userRequestDto.getFullName());
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(pwEncoder.encode(userRequestDto.getPassword()));
        userRepository.save(user);
        kafkaService.sendUserEvent(user, "UPDATED", userRequestDto);
    }

    @Transactional
    public void deleteUser(String userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("User not found", HttpStatus.BAD_REQUEST, "USER_NOT_FOUND"));
        user.getRoles().clear();
        userRepository.delete(user);
        kafkaService.sendUserEvent(user, "DELETED", null);
    }

}

package com.apica.user_service.controller;

import com.apica.user_service.dto.GetUserResponseDto;
import com.apica.user_service.dto.SuccessResponse;
import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.service.UserService;
import com.apica.user_service.utils.ApiResponseUtil;
import com.apica.user_service.utils.CustomApiException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<GetUserResponseDto>>> getUsers() {
        try {
            List<GetUserResponseDto> users = userService.getAllUsers();
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Successfully retrieved users", users);
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<GetUserResponseDto>> getUser(@PathVariable String userId) {
        try {
            GetUserResponseDto user = userService.getUser(userId);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Successfully retrieved user", user);
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> updateUser(@PathVariable String userId, @Valid @RequestBody UserRequestDto userDto) {
        try {
            userService.updateUser(userId, userDto);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Successfully updated user", userId);
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Successfully deleted user", userId);
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
    }
}

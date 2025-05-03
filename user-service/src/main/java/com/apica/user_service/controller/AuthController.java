package com.apica.user_service.controller;

import com.apica.user_service.dto.AuthRequest;
import com.apica.user_service.dto.SuccessResponse;
import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.Roles;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.security.JwtUtil;
import com.apica.user_service.service.KafkaService;
import com.apica.user_service.utils.ApiResponseUtil;
import com.apica.user_service.utils.CustomApiException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authMgr;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RolesRepository roleRepo;

    @Autowired
    private PasswordEncoder pwEncoder;

    @Autowired
    private KafkaService kafkaService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<String>> register(@Valid @RequestBody UserRequestDto req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ApiResponseUtil.ErrorResponse(HttpStatus.CONFLICT, "Username already taken", "USERNAME_TAKEN");
        }
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setPassword(pwEncoder.encode(req.getPassword()));

        // assign role:
        Set<Roles> assigned;
        try {
            assigned = req.getRoleName().stream()
                    .map(rn -> roleRepo.findByName(rn)
                            .orElseThrow(() -> new CustomApiException("Role not found: " + rn, HttpStatus.BAD_REQUEST, "ROLE_NOT_FOUND")))
                    .collect(Collectors.toSet());
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
        // If you trust no client input for roles, ignore req.getRoles() and always assign only ROLE_USER here.
        if (assigned.isEmpty()) {
            Roles defaultRole = roleRepo.findByName("ROLE_USER").orElseThrow();
            assigned = Set.of(defaultRole);
        }
        user.setRoles(assigned);
        user.setFullName(req.getFullName());
        userRepo.save(user);
        kafkaService.sendUserEvent(user, "CREATED", req);
        return ApiResponseUtil.SuccessResponse(HttpStatus.CREATED, "User registered", req.getFullName());
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<String>> login(@Valid @RequestBody AuthRequest req) {
        Authentication auth;
        try {
            auth = authMgr.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (Exception e) {
            return ApiResponseUtil.SuccessResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", req.getUsername());
        }
        String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
        return ApiResponseUtil.SuccessResponse(HttpStatus.ACCEPTED, "Token generated", token);
    }
}

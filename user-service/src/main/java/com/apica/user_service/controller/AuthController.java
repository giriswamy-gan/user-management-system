package com.apica.user_service.controller;

import com.apica.user_service.dto.AuthRequest;
import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.Roles;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.security.JwtUtil;
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

import java.util.Objects;
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


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDto req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already taken");
        }
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setPassword(pwEncoder.encode(req.getPassword()));

        // assign role:
        Roles assigned = null;
        if (Objects.nonNull(req.getRoleName()) && roleRepo.findByName(req.getRoleName()).isPresent()) {
            assigned = roleRepo.findByName(req.getRoleName()).get();
        }
        // If you trust no client input for roles, ignore req.getRoles() and always assign only ROLE_USER here.
        if (assigned == null) {
            assigned = roleRepo.findByName("ROLE_USER").orElseThrow();
        }
        user.setRoles(assigned);
        user.setFullName(req.getFullName());
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest req) {
        Authentication auth = authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(token);
    }
}

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

    AuthenticationManager authMgr;
    JwtUtil jwtUtil;
    UserRepository userRepo;
    RolesRepository roleRepo;
    PasswordEncoder pwEncoder;
    KafkaService kafkaService;

    public AuthController(AuthenticationManager authMgr, JwtUtil jwtUtil, UserRepository userRepo, RolesRepository roleRepo, PasswordEncoder pwEncoder, KafkaService kafkaService) {
        this.authMgr = authMgr;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.pwEncoder = pwEncoder;
        this.kafkaService = kafkaService;
    }

    /**
     * Registers a new user in the system.
     *
     * @param req The {@link UserRequestDto} containing the user's registration details.
     *            Includes username, password, full name, and optional roles.
     * @return A {@link ResponseEntity} containing a {@link SuccessResponse} with a message
     *         indicating the success or failure of the registration process.
     *         - Returns HTTP 201 (Created) if the user is successfully registered.
     *         - Returns HTTP 409 (Conflict) if the username is already taken.
     *         - Returns HTTP 400 (Bad Request) if a specified role is not found.
     * 
     * @throws CustomApiException If a specified role does not exist in the system.
     * 
     * <p><b>Workflow:</b></p>
     * <ul>
     *   <li>Checks if the username already exists in the database.</li>
     *   <li>Encodes the user's password using the password encoder.</li>
     *   <li>Assigns roles to the user based on the provided role names. If no roles are provided,
     *       assigns a default role of "ROLE_USER".</li>
     *   <li>Saves the user to the database.</li>
     *   <li>Sends a user creation event to Kafka for further processing.</li>
     * </ul>
     * 
     * <p><b>Note:</b> If client input for roles is not trusted, ignore the roles provided in the
     * request and always assign the default role "ROLE_USER".</p>
     */
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

    /**
     * Handles user login requests.
     *
     * @param req The authentication request containing username and password.
     * @return A ResponseEntity containing a success response with a generated token
     *         if authentication is successful, or an error response if authentication fails.
     * @throws CustomApiException if the user is not found in the database.
     *
     * This method performs the following steps:
     * 1. Authenticates the user using the provided username and password.
     * 2. If authentication fails, returns an unauthorized response with an error message.
     * 3. If authentication succeeds, generates a JWT token for the authenticated user.
     * 4. Retrieves the user from the database and sends a "LOGIN" event to Kafka.
     * 5. Returns an accepted response with the generated token.
     */
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
        Users saved = userRepo.findByUsername(req.getUsername()).orElseThrow(() -> new CustomApiException("User not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
        kafkaService.sendUserEvent(saved, "LOGIN", null);
        return ApiResponseUtil.SuccessResponse(HttpStatus.ACCEPTED, "Token generated", token);
    }
}

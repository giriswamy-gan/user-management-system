package com.apica.user_service.controller; 

import com.apica.user_service.dto.AuthRequest;
import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.Roles;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.security.JwtUtil;
import com.apica.user_service.service.KafkaService;
import com.apica.user_service.utils.CustomApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authMgr;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepo;

    @Mock
    private RolesRepository roleRepo;

    @Mock
    private PasswordEncoder pwEncoder;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    private UserRequestDto validUserRequest;
    private AuthRequest validAuthRequest;
    private Users mockUser;
    private Roles userRole;
    private Roles adminRole;

    @BeforeEach
    void setUp() {
        // Set up valid user request for registration
        validUserRequest = new UserRequestDto();
        validUserRequest.setUsername("testuser");
        validUserRequest.setPassword("password123");
        validUserRequest.setFullName("Test User");
        validUserRequest.setRoleName(List.of("ROLE_USER"));

        // Set up valid auth request for login
        validAuthRequest = new AuthRequest();
        validAuthRequest.setUsername("testuser");
        validAuthRequest.setPassword("password123");

        // Set up mock user
        mockUser = new Users();
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword");
        mockUser.setFullName("Test User");

        // Set up roles
        userRole = new Roles();
        userRole.setName("ROLE_USER");

        adminRole = new Roles();
        adminRole.setName("ROLE_ADMIN");
    }

    @Test
    void registerSuccessTest() {
        // Given
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(pwEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        // When
        ResponseEntity<?> response = authController.register(validUserRequest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepo).save(any(Users.class));
        verify(kafkaService).sendUserEvent(any(Users.class), eq("CREATED"), eq(validUserRequest));
    }

    @Test
    void registerExistingUserTest() {
        // Given
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // When
        ResponseEntity<?> response = authController.register(validUserRequest);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepo, never()).save(any(Users.class));
        verify(kafkaService, never()).sendUserEvent(any(), any(), any());
    }

    @Test
    void registerWithInvalidRoleTest() {
        // Given
        validUserRequest.setRoleName(List.of("ROLE_NONEXISTENT"));
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_NONEXISTENT")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = authController.register(validUserRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepo, never()).save(any(Users.class));
    }

    @Test
    void registerWithNoRolesTest() {
        // Given
        validUserRequest.setRoleName(Collections.emptyList());
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        // When
        ResponseEntity<?> response = authController.register(validUserRequest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepo).save(any(Users.class));
        // Verify that the default ROLE_USER was assigned
        verify(roleRepo).findByName("ROLE_USER");
    }

    @Test
    void registerWithMultipleRolesTest() {
        // Given
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        validUserRequest.setRoleName(roles);

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(roleRepo.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        // When
        ResponseEntity<?> response = authController.register(validUserRequest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepo).save(any(Users.class));
        verify(roleRepo).findByName("ROLE_USER");
        verify(roleRepo).findByName("ROLE_ADMIN");
    }

    @Test
    void loginSuccessTest() {
        // Given
        when(authMgr.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("valid.jwt.token");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // When
        ResponseEntity<?> response = authController.login(validAuthRequest);

        // Then
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(kafkaService).sendUserEvent(eq(mockUser), eq("LOGIN"), isNull());
    }

    @Test
    void loginFailedAuthenticationTest() {
        // Given
        when(authMgr.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When
        ResponseEntity<?> response = authController.login(validAuthRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(kafkaService, never()).sendUserEvent(any(), any(), any());
    }

    @Test
    void loginUserNotFoundTest() {
        // Given
        when(authMgr.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("valid.jwt.token");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomApiException.class, () -> authController.login(validAuthRequest));
    }
}
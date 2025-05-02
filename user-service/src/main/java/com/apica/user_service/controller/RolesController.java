package com.apica.user_service.controller;

import com.apica.user_service.dto.RoleRequestDto;
import com.apica.user_service.service.RolesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/roles")
@RestController
public class RolesController {

    RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping
    public ResponseEntity<String> createRoles(@RequestBody RoleRequestDto req) {
        try {
            rolesService.createRoles(req);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return new ResponseEntity<>("Role created", HttpStatus.CREATED);
    }
}

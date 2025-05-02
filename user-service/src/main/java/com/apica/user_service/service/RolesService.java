package com.apica.user_service.service;

import com.apica.user_service.dto.RoleRequestDto;
import com.apica.user_service.entity.Roles;
import com.apica.user_service.repository.RolesRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RolesService {

    RolesRepository rolesRepo;

    public RolesService(RolesRepository rolesRepo) {
        this.rolesRepo = rolesRepo;
    }

    public void createRoles(RoleRequestDto req) {
        if (Objects.isNull(req.getName())) {
            throw new RuntimeException("Name is required");
        }
        if (rolesRepo.findByName(req.getName()).isPresent()) {
            throw new RuntimeException("Role name already exists");
        }

        Roles role = new Roles();
        role.setName(req.getName());
        rolesRepo.save(role);
    }
}

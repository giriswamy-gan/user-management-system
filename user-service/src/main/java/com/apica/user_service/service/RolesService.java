package com.apica.user_service.service;

import com.apica.user_service.dto.GetRoleResponseDto;
import com.apica.user_service.dto.RoleRequestDto;
import com.apica.user_service.entity.Roles;
import com.apica.user_service.entity.Users;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.utils.CustomApiException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RolesService {

    RolesRepository rolesRepo;
    UserRepository userRepo;

    public RolesService(RolesRepository rolesRepo, UserRepository userRepo) {
        this.rolesRepo = rolesRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void createRoles(RoleRequestDto req) {
        if (Objects.isNull(req.getName())) {
            throw new CustomApiException("Name is required", HttpStatus.BAD_REQUEST, "NAME_NOT_FOUND");
        }
        if (rolesRepo.findByName(req.getName()).isPresent()) {
            throw new CustomApiException("Role name already exists", HttpStatus.BAD_REQUEST, "ROLE_NAME_EXISTS");
        }

        Roles role = new Roles();
        role.setName(req.getName());
        rolesRepo.save(role);
    }

    public List<GetRoleResponseDto> getAllRoles() {
        List<GetRoleResponseDto> dtos = new ArrayList<>();
        List<Roles> roles = rolesRepo.findAll();
        for (Roles role : roles) {
            GetRoleResponseDto dto = new GetRoleResponseDto();
            dto.setRoleId(role.getId());
            dto.setRoleName(role.getName());
            dtos.add(dto);
        }
        return dtos;
    }

    public GetRoleResponseDto getRole(String roleId) {
        Roles role = rolesRepo.findById(roleId).orElseThrow(() -> new CustomApiException("Role not found", HttpStatus.BAD_REQUEST, "ROLE_NOT_FOUND"));
        GetRoleResponseDto dto = new GetRoleResponseDto();
        dto.setRoleId(role.getId());
        dto.setRoleName(role.getName());
        return dto;
    }

    @Transactional
    public void updateRole(String roleId, RoleRequestDto req) {
        Roles role = rolesRepo.findById(roleId).orElseThrow(() -> new CustomApiException("Role not found", HttpStatus.BAD_REQUEST, "ROLE_NOT_FOUND"));
        role.setName(req.getName());
        rolesRepo.save(role);
    }

//    @Transactional
//    public void deleteRole(String roleId) {
//        Roles role = rolesRepo.findById(roleId).orElseThrow(() -> new CustomApiException("Role not found", HttpStatus.BAD_REQUEST, "ROLE_NOT_FOUND"));
//        List<Users> usersWithRole = userRepo.findAllByRolesId(roleId);
//        for (Users user : usersWithRole) {
//            user.getRoles().remove(role);
//        }
//
//        // now delete the role
//        rolesRepo.delete(role);
//    }
}

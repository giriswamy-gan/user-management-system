package com.apica.user_service.controller;

import com.apica.user_service.dto.GetRoleResponseDto;
import com.apica.user_service.dto.RoleRequestDto;
import com.apica.user_service.dto.SuccessResponse;
import com.apica.user_service.service.RolesService;
import com.apica.user_service.utils.ApiResponseUtil;
import com.apica.user_service.utils.CustomApiException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/roles")
@RestController
public class RolesController {

    RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<String>> createRoles(@Valid @RequestBody RoleRequestDto req) {
        try {
            rolesService.createRoles(req);
            return ApiResponseUtil.SuccessResponse(HttpStatus.CREATED, "Role created", req.getName());
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<GetRoleResponseDto>>> getAllRoles() {
        try {
            List<GetRoleResponseDto> roles = rolesService.getAllRoles();
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "All roles retrieved", roles);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<SuccessResponse<GetRoleResponseDto>> getRole(@PathVariable String roleId) {
        try {
            GetRoleResponseDto role = rolesService.getRole(roleId);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Role retrieved", role);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<String>> updateRole(@PathVariable String roleId, @Valid @RequestBody RoleRequestDto req) {
        try {
            rolesService.updateRole(roleId, req);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Role updated", req.getName());
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

//    @DeleteMapping("/{roleId}")
//    public ResponseEntity<SuccessResponse<String>> deleteRole(@PathVariable String roleId) {
//        try {
//            rolesService.deleteRole(roleId);
//            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Role deleted", roleId);
//        } catch (CustomApiException ex) {
//            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
//        }
//    }
}

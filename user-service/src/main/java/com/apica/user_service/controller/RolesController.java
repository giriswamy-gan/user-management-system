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

    /**
     * Handles the creation of a new role.
     *
     * @param req the request body containing the details of the role to be created.
     *            Must be a valid {@link RoleRequestDto} object.
     * @return a {@link ResponseEntity} containing a {@link SuccessResponse} with the
     *         HTTP status code, a success message, and the name of the created role.
     * @throws CustomApiException if an error occurs during role creation, returning
     *                            an appropriate error response with status code and
     *                            custom error code.
     */
    @PostMapping
    public ResponseEntity<SuccessResponse<String>> createRoles(@Valid @RequestBody RoleRequestDto req) {
        try {
            rolesService.createRoles(req);
            return ApiResponseUtil.SuccessResponse(HttpStatus.CREATED, "Role created", req.getName());
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

    /**
     * Retrieves all roles from the system.
     *
     * @return A ResponseEntity containing a SuccessResponse with a list of 
     *         GetRoleResponseDto objects representing all roles, or an error 
     *         response in case of a CustomApiException.
     */
    @GetMapping
    public ResponseEntity<SuccessResponse<List<GetRoleResponseDto>>> getAllRoles() {
        try {
            List<GetRoleResponseDto> roles = rolesService.getAllRoles();
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "All roles retrieved", roles);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param roleId The unique identifier of the role to retrieve.
     * @return A ResponseEntity containing a SuccessResponse with the role details
     *         if the operation is successful, or an error response if an exception occurs.
     * @throws CustomApiException If there is an error during the retrieval process,
     *         containing the status code, error message, and custom error code.
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<SuccessResponse<GetRoleResponseDto>> getRole(@PathVariable String roleId) {
        try {
            GetRoleResponseDto role = rolesService.getRole(roleId);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Role retrieved", role);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.ErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getCustomCode());
        }
    }

    /**
     * Updates an existing role with the provided details.
     *
     * @param roleId The unique identifier of the role to be updated.
     * @param req The request body containing the updated role details.
     * @return A ResponseEntity containing a success response with the updated role name
     *         or an error response in case of failure.
     * @throws CustomApiException If an error occurs during the update process, such as
     *         invalid input or role not found.
     * 
     * @apiNote This endpoint is secured and requires the user to have the 'ADMIN' role.
     */
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
}

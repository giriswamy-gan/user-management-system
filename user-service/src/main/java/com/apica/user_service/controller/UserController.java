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

    /**
     * Retrieves a list of all users.
     *
     * @return ResponseEntity containing a SuccessResponse with a list of 
     *         GetUserResponseDto objects if the operation is successful, or an 
     *         ErrorResponse in case of a CustomApiException.
     * @throws CustomApiException if an error occurs during the retrieval of users.
     */
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<GetUserResponseDto>>> getUsers() {
        try {
            List<GetUserResponseDto> users = userService.getAllUsers();
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Successfully retrieved users", users);
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId The unique identifier of the user to retrieve.
     * @return A ResponseEntity containing a SuccessResponse with the user details
     *         if the operation is successful, or an error response if an exception occurs.
     * @throws CustomApiException If there is an error during the retrieval process,
     *         containing the status code, error message, and custom error code.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<GetUserResponseDto>> getUser(@PathVariable String userId) {
        try {
            GetUserResponseDto user = userService.getUser(userId);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Successfully retrieved user", user);
        } catch (CustomApiException e) {
            return ApiResponseUtil.ErrorResponse(e.getStatusCode(), e.getMessage(), e.getCustomCode());
        }
    }

    /**
     * Updates the details of an existing user.
     *
     * @param userId The unique identifier of the user to be updated.
     * @param userDto The updated user details encapsulated in a {@link UserRequestDto}.
     * @return A {@link ResponseEntity} containing a {@link SuccessResponse} with a success message and the user ID if the update is successful,
     *         or an error response with the appropriate status code and error details if an exception occurs.
     * @throws CustomApiException If an error occurs during the update process, such as invalid input or user not found.
     * 
     * @see UserRequestDto
     * @see com.apica.user_service.util.ApiResponseUtil
     */
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

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @return A ResponseEntity containing a SuccessResponse with a message and the userId if the deletion is successful,
     *         or an ErrorResponse with the appropriate error details if a CustomApiException is thrown.
     * @throws CustomApiException If an error occurs during the deletion process, such as the user not being found.
     * 
     * @PreAuthorize Ensures that only users with the 'ADMIN' role can access this endpoint.
     * @DeleteMapping Maps HTTP DELETE requests to this method with the specified userId path variable.
     */
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

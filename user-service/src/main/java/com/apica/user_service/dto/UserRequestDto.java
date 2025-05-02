package com.apica.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

import java.util.List;

@Getter
@Setter
public class UserRequestDto {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;

    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only alphabets and spaces")
    @Size(min = 1, max = 100, message = "Full Name size shouldn't exceed 100")
    @NotBlank(message = "Full Name is required")
    private String fullName;

    @NotEmpty(message = "Role Name is required")
    private String roleName;
}

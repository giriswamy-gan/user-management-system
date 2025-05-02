package com.apica.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDto {
    @NotBlank(message = "Role name is required")
    private String name;
}

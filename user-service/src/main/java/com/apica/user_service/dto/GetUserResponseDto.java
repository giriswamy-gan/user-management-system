package com.apica.user_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetUserResponseDto {
    private String userId;
    private String userName;
    private String fullName;
    private List<String> roles;
}

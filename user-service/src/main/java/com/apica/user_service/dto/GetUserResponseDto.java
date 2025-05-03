package com.apica.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserResponseDto {
    private String userId;
    private String userName;
    private String fullName;
}

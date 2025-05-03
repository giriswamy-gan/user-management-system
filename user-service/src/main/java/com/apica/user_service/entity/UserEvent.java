package com.apica.user_service.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserEvent {
    private String userId;
    private String action;
    private Instant timestamp;
    private String payloadJson;
}

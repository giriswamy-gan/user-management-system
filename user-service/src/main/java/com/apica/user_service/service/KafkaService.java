package com.apica.user_service.service;

import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.UserEvent;
import com.apica.user_service.entity.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class KafkaService {

    KafkaTemplate<String, String> kafka;
    ObjectMapper objectMapper;

    public KafkaService(KafkaTemplate<String, String> kafka, ObjectMapper objectMapper) {
        this.kafka = kafka;
        this.objectMapper = objectMapper;
    }

    @Value("${kafka.topic.user-events}") private String topic;


    public void sendUserEvent(Users saved, String action, UserRequestDto req) {
        UserEvent ev = new UserEvent();
        ev.setUserId(saved.getId());
        ev.setAction(action);
        ev.setTimestamp(Instant.now());
        try {
            String payload = objectMapper.writeValueAsString(req);
            ev.setPayloadJson(payload);
            String json = objectMapper.writeValueAsString(ev);

            kafka.send(topic, saved.getId(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}

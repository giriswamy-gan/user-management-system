package com.apica.user_service.service;

import com.apica.user_service.entity.Users;
import com.apica.user_service.dto.UserRequestDto;
import com.apica.user_service.entity.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaService kafkaService;

    @BeforeEach
    void init() {
        // inject the @Value topic
        ReflectionTestUtils.setField(kafkaService, "topic", "user-events");
    }

    @Test
    void sendUserEvent_whenSerializationSucceeds_sendsJsonToKafka() throws Exception {
        // prepare
        Users saved = new Users();
        saved.setId("u1");
        UserRequestDto req = new UserRequestDto();
        req.setUsername("bob");
        req.setFullName("Bob Smith");
        req.setPassword("secret");

        // first call serializes req → payloadJson
        when(objectMapper.writeValueAsString(req)).thenReturn("{\"username\":\"bob\"}");
        // second call serializes the UserEvent → full event JSON
        when(objectMapper.writeValueAsString(Mockito.isA(UserEvent.class))).thenReturn("{\"userId\":\"u1\",\"action\":\"CREATED\"}");

        // exercise
        kafkaService.sendUserEvent(saved, "CREATED", req);

        // verify that the template was called with the final JSON
        verify(kafkaTemplate).send("user-events", "u1", "{\"userId\":\"u1\",\"action\":\"CREATED\"}");
    }

    @Test
    void sendUserEvent_whenSerializationFails_throwsRuntimeException() throws Exception {
        Users saved = new Users();
        saved.setId("u2");
        UserRequestDto req = new UserRequestDto();

        // simulate failure on serializing req
        when(objectMapper.writeValueAsString(req))
                .thenThrow(new JsonProcessingException("boom"){});

        // assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                kafkaService.sendUserEvent(saved, "UPDATED", req)
        );
        assertThat(ex).hasMessageContaining("Failed to serialize payload");

        // ensure no Kafka send attempted
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}

package com.apica.journal_service.service;

import com.apica.journal_service.entity.JournalEntry;
import com.apica.journal_service.repository.JournalRepository;
import com.apica.journal_service.entity.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalListenerTest {

    @Mock
    private JournalRepository repo;

    private ObjectMapper mapper;
    private JournalListener listener;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        listener = new JournalListener(repo, mapper);
    }

    @Test
    void onUserEvent_validJson_savesJournalEntry() throws Exception {
        // arrange: build a UserEvent and its JSON
        UserEvent ev = new UserEvent();
        ev.setUserId("use-63b8353e-ae18-43dc-9f08-537100bfe447");
        ev.setAction("CREATED");
        ev.setTimestamp(Instant.now());
        ev.setPayloadJson("{\"foo\":\"bar\"}");

        String json = mapper.writeValueAsString(ev);

        // act
        listener.onUserEvent(json);

        // assert: capture the JournalEntry saved
        ArgumentCaptor<JournalEntry> captor = ArgumentCaptor.forClass(JournalEntry.class);
        verify(repo).save(captor.capture());

        JournalEntry entry = captor.getValue();
        assertThat(entry.getUserId()).isEqualTo(ev.getUserId());
        assertThat(entry.getAction()).isEqualTo(ev.getAction());
        assertThat(entry.getTimestamp()).isEqualTo(ev.getTimestamp());
        assertThat(entry.getPayload()).isEqualTo(ev.getPayloadJson());
    }

    @Test
    void onUserEvent_malformedJson_throwsRuntimeException() {
        String badJson = "{ not valid JSON }";

        // act & assert
        assertThatThrownBy(() -> listener.onUserEvent(badJson))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);

        // repository should never be touched
        verifyNoInteractions(repo);
    }
}

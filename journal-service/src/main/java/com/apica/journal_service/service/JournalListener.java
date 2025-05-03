package com.apica.journal_service.service;

import com.apica.journal_service.entity.JournalEntry;
import com.apica.journal_service.entity.UserEvent;
import com.apica.journal_service.repository.JournalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JournalListener {

    JournalRepository repo;
    ObjectMapper mapper;

    public JournalListener(JournalRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${kafka.topic.user-events}", groupId = "journal-group")
    public void onUserEvent(String json) {
        JournalEntry entry = new JournalEntry();
        UserEvent ev = null;
        try {
            ev = mapper.readValue(json, UserEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        entry.setUserId(ev.getUserId());
        entry.setAction(ev.getAction());
        entry.setTimestamp(ev.getTimestamp());
        entry.setPayload(ev.getPayloadJson());
        repo.save(entry);
    }
}

package com.apica.journal_service.entity;

import com.apica.journal_service.utils.CustomIdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class JournalEntry {
    @Id
    String id;
    String userId;
    String action;
    Instant timestamp;
    @Column(columnDefinition="text")
    String payload;

    public JournalEntry() {
        this.id = CustomIdGenerator.generateId(this.getClass().getSimpleName());
    }
}

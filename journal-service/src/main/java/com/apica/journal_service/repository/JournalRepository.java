package com.apica.journal_service.repository;

import com.apica.journal_service.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalRepository extends JpaRepository<JournalEntry, String> {
    List<JournalEntry> findAllByActionIgnoreCase(String action);
}

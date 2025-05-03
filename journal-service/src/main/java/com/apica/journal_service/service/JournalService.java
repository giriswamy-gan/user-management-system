package com.apica.journal_service.service;

import com.apica.journal_service.dto.GetJournalEntryDto;
import com.apica.journal_service.entity.JournalEntry;
import com.apica.journal_service.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JournalService {

    JournalRepository journalRepo;

    public JournalService(JournalRepository journalRepo) {
        this.journalRepo = journalRepo;
    }

    public List<GetJournalEntryDto> getAllJournalEntries() {
        List<GetJournalEntryDto> dtos = new ArrayList<>();

        List<JournalEntry> journalEntries = journalRepo.findAll();
        for (JournalEntry journalEntry : journalEntries) {
            GetJournalEntryDto dto = new GetJournalEntryDto();
            dto.setJournalEntryId(journalEntry.getId());
            dto.setUserId(journalEntry.getUserId());
            dto.setAction(journalEntry.getAction());
            dto.setTimeStamp(journalEntry.getTimestamp().toString());
            dto.setPayload(journalEntry.getPayload());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<GetJournalEntryDto> getJournalEntriesBasedOn(String action) {
        List<GetJournalEntryDto> dtos = new ArrayList<>();
        List<JournalEntry> journalEntries = journalRepo.findAllByActionIgnoreCase(action);
        for (JournalEntry journalEntry : journalEntries) {
            GetJournalEntryDto dto = new GetJournalEntryDto();
            dto.setJournalEntryId(journalEntry.getId());
            dto.setUserId(journalEntry.getUserId());
            dto.setAction(journalEntry.getAction());
            dto.setTimeStamp(journalEntry.getTimestamp().toString());
            dto.setPayload(journalEntry.getPayload());
            dtos.add(dto);
        }
        return dtos;
    }
}

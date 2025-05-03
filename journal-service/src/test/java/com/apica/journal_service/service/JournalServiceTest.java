package com.apica.journal_service.service;

import com.apica.journal_service.dto.GetJournalEntryDto;
import com.apica.journal_service.entity.JournalEntry;
import com.apica.journal_service.repository.JournalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalRepository journalRepo;

    @InjectMocks
    private JournalService journalService;

    private JournalEntry entry1;
    private JournalEntry entry2;

    @BeforeEach
    void setUp() {
        entry1 = new JournalEntry();
        entry1.setId("jou-96e950bc-19a5-47d8-8b57-9308c099fr24");
        entry1.setUserId("use-63b8353e-ae18-43dc-9f08-537100bfe558");
        entry1.setAction("CREATED");
        entry1.setTimestamp(Instant.parse("2025-05-03T00:00:00Z"));
        entry1.setPayload("payload1");

        entry2 = new JournalEntry();
        entry2.setId("jou-96e950bc-19a5-47d8-8b57-9308c099fr24");
        entry2.setUserId("use-63b8353e-ae18-43dc-9f08-537100bfe559");
        entry2.setAction("DELETED");
        entry2.setTimestamp(Instant.parse("2025-05-03T01:00:00Z"));
        entry2.setPayload("payload2");
    }

    @Test
    void getAllJournalEntries_returnsMappedList() {
        // given
        given(journalRepo.findAll()).willReturn(List.of(entry1, entry2));

        // when
        List<GetJournalEntryDto> dtos = journalService.getAllJournalEntries();

        // then
        assertThat(dtos).hasSize(2);

        GetJournalEntryDto dto1 = dtos.get(0);
        assertThat(dto1.getJournalEntryId()).isEqualTo("jou-96e950bc-19a5-47d8-8b57-9308c099fr24");
        assertThat(dto1.getUserId()).isEqualTo(entry1.getUserId());
        assertThat(dto1.getAction()).isEqualTo("CREATED");
        assertThat(dto1.getTimeStamp()).isEqualTo(entry1.getTimestamp().toString());
        assertThat(dto1.getPayload()).isEqualTo("payload1");

        GetJournalEntryDto dto2 = dtos.get(1);
        assertThat(dto2.getJournalEntryId()).isEqualTo("jou-96e950bc-19a5-47d8-8b57-9308c099fr24");
        assertThat(dto2.getUserId()).isEqualTo(entry2.getUserId());
        assertThat(dto2.getAction()).isEqualTo("DELETED");
        assertThat(dto2.getTimeStamp()).isEqualTo(entry2.getTimestamp().toString());
        assertThat(dto2.getPayload()).isEqualTo("payload2");

        then(journalRepo).should().findAll();
    }

    @Test
    void getAllJournalEntries_emptyList_returnsEmpty() {
        given(journalRepo.findAll()).willReturn(List.of());

        List<GetJournalEntryDto> dtos = journalService.getAllJournalEntries();

        assertThat(dtos).isEmpty();
        then(journalRepo).should().findAll();
    }

    @Test
    void getJournalEntriesBasedOn_returnsFilteredMappedList() {
        // given
        given(journalRepo.findAllByActionIgnoreCase("created"))
                .willReturn(List.of(entry1));

        // when
        List<GetJournalEntryDto> dtos = journalService.getJournalEntriesBasedOn("created");

        // then
        assertThat(dtos).hasSize(1);
        GetJournalEntryDto dto = dtos.get(0);
        assertThat(dto.getJournalEntryId()).isEqualTo(entry1.getId());
        assertThat(dto.getUserId()).isEqualTo(entry1.getUserId());
        assertThat(dto.getAction()).isEqualTo(entry1.getAction());
        assertThat(dto.getTimeStamp()).isEqualTo(entry1.getTimestamp().toString());
        assertThat(dto.getPayload()).isEqualTo(entry1.getPayload());

        then(journalRepo).should().findAllByActionIgnoreCase("created");
    }

    @Test
    void getJournalEntriesBasedOn_noMatches_returnsEmpty() {
        given(journalRepo.findAllByActionIgnoreCase("unknown"))
                .willReturn(List.of());

        List<GetJournalEntryDto> dtos = journalService.getJournalEntriesBasedOn("unknown");

        assertThat(dtos).isEmpty();
        then(journalRepo).should().findAllByActionIgnoreCase("unknown");
    }
}

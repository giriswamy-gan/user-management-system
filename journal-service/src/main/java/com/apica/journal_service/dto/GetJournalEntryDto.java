package com.apica.journal_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetJournalEntryDto {
    private String journalEntryId;
    private String userId;
    private String action;
    private String timeStamp;
    private String payload;
}

package com.apica.journal_service.controller;

import com.apica.journal_service.dto.GetJournalEntryDto;
import com.apica.journal_service.dto.SuccessResponse;
import com.apica.journal_service.service.JournalService;
import com.apica.journal_service.utils.ApiResponseUtil;
import com.apica.journal_service.utils.CustomApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {

    JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<GetJournalEntryDto>>> getAllJournalEntries() {
        try {
            List<GetJournalEntryDto> res = journalService.getAllJournalEntries();
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Journal Entries Retrieved", res);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.SuccessResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<GetJournalEntryDto>>> getJournalEntriesBasedOn(@RequestParam("action") String action) {
        try {
            List<GetJournalEntryDto> res = journalService.getJournalEntriesBasedOn(action);
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Journal Entries", res);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.SuccessResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        }
    }
}

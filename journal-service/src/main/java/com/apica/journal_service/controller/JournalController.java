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

    /**
     * Retrieves all journal entries.
     *
     * @return A ResponseEntity containing a SuccessResponse with a list of 
     *         GetJournalEntryDto objects and an HTTP status code. If successful, 
     *         the response will include the journal entries and a status of OK. 
     *         In case of an error, it will return an INTERNAL_SERVER_ERROR status 
     *         with the corresponding error message.
     */
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<GetJournalEntryDto>>> getAllJournalEntries() {
        try {
            List<GetJournalEntryDto> res = journalService.getAllJournalEntries();
            return ApiResponseUtil.SuccessResponse(HttpStatus.OK, "Journal Entries Retrieved", res);
        } catch (CustomApiException ex) {
            return ApiResponseUtil.SuccessResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        }
    }

    /**
     * Retrieves a list of journal entries based on the specified action.
     *
     * @param action The action parameter used to filter journal entries.
     *               This parameter determines which journal entries to retrieve.
     * @return A ResponseEntity containing a SuccessResponse with a list of 
     *         GetJournalEntryDto objects if successful, or an error message 
     *         if an exception occurs.
     * @throws CustomApiException If an error occurs while retrieving journal entries.
     */
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

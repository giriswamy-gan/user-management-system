package com.apica.journal_service.utils;

import com.apica.journal_service.dto.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    /**
     * Utility method to create a success response with a generic payload.
     */
    public static <T> ResponseEntity<SuccessResponse<T>> SuccessResponse(
            HttpStatus status, String message, T data) {
        SuccessResponse<T> apiResponse = new SuccessResponse<>(
                status.value(),
                new SuccessResponse.DataWrapper<>(message, data, "")
        );
        return new ResponseEntity<>(apiResponse, status);
    }

    /**
     * Utility method to create an error response with a message and error code.
     */
    public static <T> ResponseEntity<SuccessResponse<T>> ErrorResponse(
            HttpStatus status, String message, String customCode) {
        SuccessResponse<T> apiResponse = new SuccessResponse<>(
                status.value(),
                new SuccessResponse.DataWrapper<>(message, null, customCode)
        );
        return new ResponseEntity<>(apiResponse, status);
    }
}

package com.apica.user_service.security;

import com.apica.user_service.utils.ApiResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public final void commence(final HttpServletRequest request,
                               final HttpServletResponse response,
                               final AuthenticationException e) throws IOException {
        // Retrieve custom error attributes set in request
        String errorMessage = getErrorMessageFromRequest(request, e);

        log.error("Could not set user authentication in security context. Error: {}", errorMessage);

        // Create a standardized error response
        ResponseEntity<?> responseEntity = ApiResponseUtil.ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid Token",
                "INVALID_TOKEN"
        );

        // Write the response back to the client
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }

    /**
     * Helper method to extract error message from request attributes or default to the exception message.
     */
    private String getErrorMessageFromRequest(HttpServletRequest request, AuthenticationException e) {
        String[] errorAttributes = {"expired", "unsupported", "invalid", "illegal", "notfound"};
        for (String attribute : errorAttributes) {
            String errorMessage = (String) request.getAttribute(attribute);
            if (errorMessage != null) {
                return errorMessage;
            }
        }
        return e.getMessage(); // Fallback to exception message
    }
}
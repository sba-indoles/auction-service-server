package org.indoles.autionserviceserver.global.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {

    private final HttpStatus status;
    private final String code;
    private final String message;

    public static ExceptionResponse from(HttpStatus status, String code, String message) {
        return new ExceptionResponse(status, code, message);
    }
}

package org.indoles.autionserviceserver.global.exception;

import org.indoles.autionserviceserver.global.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.indoles.autionserviceserver.global.exception.CommonExceptionCode.COMMON_NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        CommonExceptionCode commonExceptionCode = COMMON_NOT_FOUND;

        ExceptionResponse exceptionResponse = ExceptionResponse.from(
                commonExceptionCode.getStatus(),
                commonExceptionCode.getCode(),
                commonExceptionCode.getMessage()
        );

        return ResponseEntity.status(commonExceptionCode.getStatus()).body(exceptionResponse);
    }
}

package org.indoles.autionserviceserver.global.exception;

public record ErrorResponse(
        String message,
        String errorCode
) {

    public static ErrorResponse of(final String message, final String errorCode) {
        return new ErrorResponse(message, errorCode);
    }
}

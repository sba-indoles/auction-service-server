package org.indoles.autionserviceserver.global.exception;

public class AuthorizationException extends CustomException {

    public AuthorizationException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }
}


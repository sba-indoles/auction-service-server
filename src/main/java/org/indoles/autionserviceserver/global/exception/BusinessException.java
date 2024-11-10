package org.indoles.autionserviceserver.global.exception;

public abstract class BusinessException extends RuntimeException{

    private final ExceptionCode exceptionCode;
    private final Object[] args;

    protected BusinessException(final ExceptionCode exceptionCode, final Object... args) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        this.args = args;
    }
}


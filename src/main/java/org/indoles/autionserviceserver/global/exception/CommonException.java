package org.indoles.autionserviceserver.global.exception;

public class CommonException extends BusinessException {

    public CommonException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode, args);
    }
}


package org.indoles.autionserviceserver.core.member.entity.exception;

public class MemberException extends BusinessException {

    public MemberException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode, args);
    }
}

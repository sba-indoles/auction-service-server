package org.indoles.autionserviceserver.core.member.entity.exception;

import org.indoles.autionserviceserver.global.exception.BusinessException;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;

public class MemberException extends BusinessException {

    public MemberException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode, args);
    }
}

package org.indoles.autionserviceserver.core.member.dto.request;

import org.indoles.autionserviceserver.core.member.entity.exception.MemberException;
import org.indoles.autionserviceserver.core.member.entity.exception.MemberExceptionCode;

import java.util.Objects;

public record SignInRequestInfo(
        String signInId,
        String password
) {

    public SignInRequestInfo {
        validateNotNull(signInId, "아이디");
        validateNotNull(password, "비밀번호");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new MemberException(MemberExceptionCode.FIELD_CANNOT_BE_NULL, fieldName);
        }
    }
}

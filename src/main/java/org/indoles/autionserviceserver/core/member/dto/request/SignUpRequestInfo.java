package org.indoles.autionserviceserver.core.member.dto.request;

import org.indoles.autionserviceserver.core.member.entity.exception.MemberException;

import java.util.Objects;

import static org.indoles.autionserviceserver.core.member.entity.exception.MemberExceptionCode.*;

public record SignUpRequestInfo(
        String signUpId,
        String password,
        String userRole
) {

    public SignUpRequestInfo {
        validateNotNull(signUpId, "회원가입 ID");
        validateNotNull(password, "회원가입 패스워드");
        validateNotNull(userRole, "사용자 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new MemberException(FIELD_CANNOT_BE_NULL, fieldName, fieldName);
        }
    }
}

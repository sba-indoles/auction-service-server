package org.indoles.autionserviceserver.core.auction.dto;

import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;

import java.util.Objects;

/**
 * 경매 서버에서 필요한 회원 서버의 내용을 가져오는 DTO
 */

public record SignInInfo(Long id, Role role) {
    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public SignInInfo {
        validateNotNull(id, "로그인한 사용자의 식별자");
        validateNotNull(role, "로그인한 사용자의 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isType(Role role) {
        return this.role.equals(role);
    }
}


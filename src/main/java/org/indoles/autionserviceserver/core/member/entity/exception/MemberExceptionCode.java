package org.indoles.autionserviceserver.core.member.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    ROLE_NOT_FOUND(NOT_FOUND, "MEM-001", "사용자의 Role을 찾을 수 없습니다."),
    POINT_NOT_ENOUGH(BAD_REQUEST, "MEM-002", "포인트가 부족합니다."),
    POINT_OVER_MAX(BAD_REQUEST, "MEM-003", "포인트가 최대치를 초과했습니다"),
    NUMBER_NOT_POSITIVE(BAD_REQUEST, "MEM-004", "포인트는 양수여야 합니다"),
    ID_IS_BLANK(BAD_REQUEST, "MEM-005", "아이디는 빈칸 또는 공백일 수 없습니다."),
    ID_LENGTH(BAD_REQUEST, "MEM-006", "아이디는 3글자 이상 20자 이하로 입력해주세요."),
    PASSWORD_IS_BLANK(BAD_REQUEST, "MEM-007", "비밀번호는 빈칸 또는 공백일 수 없습니다."),
    PASSWORD_LENGTH(BAD_REQUEST, "MEM-008", "비밀번호는 9글자 이상 20자 이하로 입력해주세요."),
    PASSWORD_DIGIT_REQUIRED(BAD_REQUEST, "MEM-009", "비밀번호는 숫자를 포함해야 합니다."),
    PASSWORD_LOWERCASE_REQUIRED(BAD_REQUEST, "MEM-010", "비밀번호는 소문자를 포함해야 합니다."),
    PASSWORD_VALID_CHARACTERS(BAD_REQUEST, "MEM-011", "비밀번호는 영문자와 숫자만 사용해야 합니다."),
    FIELD_CANNOT_BE_NULL(BAD_REQUEST, "MEM-012", "%s은(는) 필수 입력 값 입니다."),
    ALREADY_EXISTS(BAD_REQUEST, "MEM-013", "이미 존재하는 회원입니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "MEM-014", "회원을 찾을 수 없습니다."),
    WRONG_PASSWORD(BAD_REQUEST, "MEM-015", "패스워드가 일치하지 않습니다.."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

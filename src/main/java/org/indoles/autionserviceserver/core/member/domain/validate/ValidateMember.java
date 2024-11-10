package org.indoles.autionserviceserver.core.member.domain.validate;

import org.indoles.autionserviceserver.core.member.entity.exception.MemberException;
import org.indoles.autionserviceserver.core.member.entity.exception.MemberExceptionCode;

import java.util.regex.Pattern;

public class ValidateMember {

    private static final int MINMUM_ID_LENGTH = 2;
    private static final int MAXIMUM_ID_LENGTH = 20;
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 20;
    private static final Pattern PASSWORD_DIGIT_REQUIRED = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_LOWERCASE_REQUIRED = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_VALID_CHARACTERS = Pattern.compile("^[a-zA-Z0-9]*$");

    /**
     * 아이디 유효성 검사
     */

    public void validateSignInId(String signInId) {
        if (signInId == null || signInId.isBlank()) {
            throw new MemberException(MemberExceptionCode.ID_IS_BLANK);
        }

        if (signInId.length() < MINMUM_ID_LENGTH || signInId.length() > MAXIMUM_ID_LENGTH) {
            throw new MemberException(MemberExceptionCode.ID_LENGTH, MINMUM_ID_LENGTH, MAXIMUM_ID_LENGTH, signInId.length());
        }
    }

    /**
     * 비밀번호 유효성 검사
     */

    public void validateSignInPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new MemberException(MemberExceptionCode.PASSWORD_IS_BLANK);
        }

        if (password.length() < MINIMUM_PASSWORD_LENGTH || password.length() > MAXIMUM_PASSWORD_LENGTH) {
            throw new MemberException(MemberExceptionCode.PASSWORD_LENGTH, MINIMUM_PASSWORD_LENGTH, MAXIMUM_PASSWORD_LENGTH, password.length());
        }

        if (!PASSWORD_DIGIT_REQUIRED.matcher(password).matches()) {
            throw new MemberException(MemberExceptionCode.PASSWORD_DIGIT_REQUIRED);
        }

        if (!PASSWORD_LOWERCASE_REQUIRED.matcher(password).matches()) {
            throw new MemberException(MemberExceptionCode.PASSWORD_LOWERCASE_REQUIRED);
        }

        if (!PASSWORD_VALID_CHARACTERS.matcher(password).matches()) {
            throw new MemberException(MemberExceptionCode.PASSWORD_VALID_CHARACTERS);
        }
    }
}

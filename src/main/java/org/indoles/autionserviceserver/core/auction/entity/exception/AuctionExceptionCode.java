package org.indoles.autionserviceserver.core.auction.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum AuctionExceptionCode implements ExceptionCode {

    CONVERT_TO_STRING_ERROR(INTERNAL_SERVER_ERROR, "AUC-001","해당 객체를 String으로 변환할 수 없습니다."),
    CONVERT_TO_INVALID_TYPE(BAD_REQUEST, "AUC-002","해당 type으로 변환할 수 없습니다."),
    CONVERT_TO_FAILED_PRICE_POLICY_TYPE(BAD_REQUEST, "AUC-003","해당 JSON을 PricePolicy 객체로 변환하는 데 실패했습니다."),
    CONVERT_TO_INVALID_PRICE_POLICY_TYPE(BAD_REQUEST, "AUC-004","유효하지 않은 PricePolicyType 객체입니다.")
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

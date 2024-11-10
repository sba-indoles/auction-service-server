package org.indoles.autionserviceserver.core.auction.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum AuctionExceptionCode implements ExceptionCode {

    CONVERT_TO_STRING_ERROR(INTERNAL_SERVER_ERROR, "AUC-001", "해당 객체를 String으로 변환할 수 없습니다."),
    CONVERT_TO_INVALID_TYPE(BAD_REQUEST, "AUC-002", "해당 type으로 변환할 수 없습니다."),
    CONVERT_TO_FAILED_PRICE_POLICY_TYPE(BAD_REQUEST, "AUC-003", "해당 JSON을 PricePolicy 객체로 변환하는 데 실패했습니다."),
    CONVERT_TO_INVALID_PRICE_POLICY_TYPE(BAD_REQUEST, "AUC-004", "유효하지 않은 PricePolicyType 객체입니다."),
    AUCTION_DURATION_OVER_MAX(BAD_REQUEST, "AUC-005", "경매 지속 시간은 최대 60분까지만 가능합니다."),
    AUCTION_DURATION_NOT_MINUTE_UNIT(BAD_REQUEST, "AUC-006", "경매 지속 시간은 정확히 분 단위여야 합니다."),
    AUCTION_VARIATION_DURATION_INVALID(BAD_REQUEST, "AUC-007", "가격 할인 시간 정책이 유효하지 않습니다."),
    AUCTION_MINIMUM_PRICE(BAD_REQUEST, "AUC-008", "경매 진행 중 최소 가격은 0원 이상이어야 합니다."),
    AUCTION_MINIMUM_REFUND_STOCK_REQUIRED(BAD_REQUEST, "AUC-009", "환불할 재고는 1개보다 작을 수 없습니다."),
    AUCTION_REFUND_EXCEEDS_ORIGINAL_STOCK(BAD_REQUEST, "AUC-010", "환불 후 재고는 원래 재고보다 많을 수 없습니다"),
    AUCTION_NOT_RUNNING(BAD_REQUEST, "AUC-011", "경매가 진행 중이 아닙니다."),
    AUCTION_PRICE_MISMATCH(BAD_REQUEST, "AUC-012", "입력한 가격으로 상품을 구매할 수 없습니다."),
    INVALID_PURCHASE_QUANTITY(BAD_REQUEST, "AUC-013", "구매할 수량이 유효하지 않습니다."),
    STOCK_NOT_ENOUGH(BAD_REQUEST, "AUC-014", "재고가 부족합니다."),
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

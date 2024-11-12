package org.indoles.autionserviceserver.core.auction.domain.validate;


import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;

public class ValidatePercentPrice {

    /**
     * 할인율 유효성 검사
     *
     * @param discountRate
     * @throws BadRequestException 할인율이 0% 초과 50% 이하가 아닐 경우 발생
     */

    public static void validateDiscountRate(double discountRate) {

        final double MINIMUM_RATE = 0.0;
        final double MAXIMUM_RATE = 50.0;

        if (discountRate < MINIMUM_RATE || discountRate > MAXIMUM_RATE) {
            String message = String.format("할인율은 %d%% 초과 %d%% 이하여야 합니다. 할인율: %f%%", (int) MINIMUM_RATE,
                    (int) MAXIMUM_RATE,
                    discountRate);
            throw new BadRequestException(message, ErrorCode.A009);
        }
    }
}

package org.indoles.autionserviceserver.core.auction.domain.validate;

import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;

import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.INVALID_DISCOUNT_PERCENT_RATE;

public class ValidatePercentPrice {

    /**
     * 할인율 유효성 검사
     * @param discountPercent
     * @throws AuctionException
     */

    public static void validateDiscountRate(double discountPercent){

        final double MINIMUM_RATE = 0.0;
        final double MAXIMUM_RATE = 50.0;

        if (discountPercent < MINIMUM_RATE || discountPercent > MAXIMUM_RATE) {
            throw new AuctionException(INVALID_DISCOUNT_PERCENT_RATE);
        }
    }
}

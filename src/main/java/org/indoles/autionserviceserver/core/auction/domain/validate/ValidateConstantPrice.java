package org.indoles.autionserviceserver.core.auction.domain.validate;

import org.apache.coyote.BadRequestException;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode;

import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.AUCTION_VARIATION_WIDTH_INVALID;

public class ValidateConstantPrice {

    /**
     * 가격 변동폭 유효성 검사(0보다 큰 값 인지 검사)
     *
     * @param variationWidth 가격 변동폭
     * @throws AuctionException
     */

    public static void validateVariationWidth(long variationWidth) {

        if (variationWidth <= 0) {
            throw new AuctionException(AUCTION_VARIATION_WIDTH_INVALID);
        }
    }

    /**
     * 가격 변동폭이 상품 원가보다 큰지 검사
     *
     * @param price          상품 원가
     * @param variationWidth 가격 변동폭
     */
    public static void validateVariationWidthOverPrice(long price, long variationWidth) {
        if (price <= variationWidth) {
            throw new AuctionException(AuctionExceptionCode.INVALID_DISCOUNT_CONSTANT_RATE);
        }
    }
}

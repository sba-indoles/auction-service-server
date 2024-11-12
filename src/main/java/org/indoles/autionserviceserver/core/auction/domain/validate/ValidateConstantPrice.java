package org.indoles.autionserviceserver.core.auction.domain.validate;


import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;


public class ValidateConstantPrice {

    /**
     * 가격 변동폭 유효성 검사(0보다 큰 값 인지 검사)
     *
     * @param variationWidth 가격 변동폭
     * @throws BadRequestException 가격 변동폭이 0보다 작을 경우 발생
     */

    public static void validateVariationWidth(long variationWidth) {

        if (variationWidth <= 0) {
            throw new BadRequestException("가격 변동폭은 0보다 커야 합니다.", ErrorCode.A004);
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
            throw new BadRequestException(
                    String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", price, variationWidth),
                    ErrorCode.A008);
        }
    }
}

package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Getter;
import org.indoles.autionserviceserver.core.auction.domain.enums.PricePolicyType;

import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidatePercentPrice.*;

@Getter
public class PercentagePricePolicy implements PricePolicy {

    private final PricePolicyType type = PricePolicyType.PERCENTAGE;
    private double discountRate;

    public PercentagePricePolicy() {
    }

    public PercentagePricePolicy(double discountRate) {
        validateDiscountRate(discountRate);
        this.discountRate = discountRate;
    }

    /**
     * 할인율을 적용한 가격을 계산
     *
     * @param price
     * @param variationCount
     * @return 할인이 적용된 가격
     */

    @Override
    public long calculatePriceAtVariation(long price, long variationCount) {
        long discountedPrice = price;
        double discountFactor = (100 - discountRate) / 100.0;

        for (int i = 0; i < variationCount; i++) {
            discountedPrice = (long) Math.floor(discountedPrice * discountFactor);
        }

        return discountedPrice;
    }

    /**
     * 가격 정책의 타입을 반환
     *
     * @return PricePolicyType
     */

    @Override
    public PricePolicyType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PercentagePricePolicy that = (PercentagePricePolicy) o;
        return Double.compare(discountRate, that.discountRate) == 0 && type == that.type;
    }
}

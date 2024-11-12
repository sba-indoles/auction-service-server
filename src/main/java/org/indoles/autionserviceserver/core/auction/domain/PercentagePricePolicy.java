package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Getter;
import org.indoles.autionserviceserver.core.auction.domain.enums.PricePolicyType;
import org.indoles.autionserviceserver.core.auction.domain.validate.ValidatePercentPrice;

import java.util.Objects;

@Getter
public class PercentagePricePolicy implements PricePolicy {

    private PricePolicyType pricePolicyType = PricePolicyType.PERCENTAGE;

    private final double percentage;

    public PercentagePricePolicy(double percentage) {
        ValidatePercentPrice.validateDiscountRate(percentage);
        this.percentage = percentage;
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
        double discountFactor = (100 - percentage) / 100.0;

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
        return pricePolicyType;
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
        return Double.compare(percentage, that.percentage) == 0 && pricePolicyType == that.pricePolicyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pricePolicyType, percentage);
    }
}

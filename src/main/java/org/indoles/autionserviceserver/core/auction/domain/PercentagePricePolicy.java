package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Getter;
import org.indoles.autionserviceserver.core.auction.entity.enums.PricePolicyType;

@Getter
public class PercentagePricePolicy implements PricePolicy {

    private PricePolicyType pricePolicyType = PricePolicyType.PERCENTAGE;

    private final double percentage;

    public PercentagePricePolicy(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public long calculatePriceAtVariation(long price, long variationCount) {
        return 0;
    }

    @Override
    public PricePolicyType getType() {
        return null;
    }
}

package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Getter;
import org.indoles.autionserviceserver.core.auction.domain.enums.PricePolicyType;

@Getter
public class ConstantPricePolicy implements PricePolicy {

    private PricePolicyType pricePolicyType = PricePolicyType.CONSTANT;
    private long variationWidth;

    public ConstantPricePolicy(long variationWidth) {

        this.variationWidth = variationWidth;
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

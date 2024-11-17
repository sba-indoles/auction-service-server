package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Getter;
import org.indoles.autionserviceserver.core.auction.domain.enums.PricePolicyType;

import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateConstantPrice.*;
import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateConstantPrice.validateVariationWidth;

@Getter
public class ConstantPricePolicy implements PricePolicy {

    private PricePolicyType type = PricePolicyType.CONSTANT;
    private long variationWidth;

    public ConstantPricePolicy() {
    }

    public ConstantPricePolicy(long variationWidth) {
        validateVariationWidth(variationWidth);
        this.variationWidth = variationWidth;
    }

    @Override
    public long calculatePriceAtVariation(long price, long variationCount) {
        validateVariationWidthOverPrice(price, variationWidth);

        return price - variationCount * variationWidth;
    }

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
        ConstantPricePolicy that = (ConstantPricePolicy) o;
        return variationWidth == that.variationWidth && type == that.type;
    }
}

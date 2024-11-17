package org.indoles.autionserviceserver.core.entity;

import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PercentagePricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.enums.PricePolicyType;
import org.indoles.autionserviceserver.core.auction.entity.utils.PricePolicyConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PricePolicyConverterTest {

    private PricePolicyConverter pricePolicyConverter = new PricePolicyConverter();

    @Nested
    class PercentPricePolicy {

        @Test
        @DisplayName("올바른 퍼센트 할인 가격정책 객체를 JSON으로 변환한다")
        void transfer_PercentagePricePolicy_ToJson() {
            // given
            PricePolicy percentagePricePolicy = new PercentagePricePolicy(10);

            // when
            String json = pricePolicyConverter.convertToDatabaseColumn(percentagePricePolicy);

            // then
            assertThat(json).isEqualTo("{\"type\":\"PERCENTAGE\",\"discountRate\":10.0}");
        }

        @Test
        @DisplayName("올바른 JSON을 퍼센트 할인 가격정책 객체로 변환한다")
        void transfer_Json_ToPercentagePricePolicy() {
            // given
            String json = "{\"type\":\"PERCENTAGE\",\"discountRate\":10.0}";

            // when
            PricePolicy pricePolicy = pricePolicyConverter.convertToEntityAttribute(json);

            // then
            assertAll(
                    () -> assertThat(pricePolicy.getType()).isEqualTo(PricePolicyType.PERCENTAGE),
                    () -> assertThat(((PercentagePricePolicy) pricePolicy).getDiscountRate()).isEqualTo(10.0)
            );
        }
    }

    @Test
    @DisplayName("올바른 고정 할인 가격정책 객체를 JSON으로 변환한다")
    void transfer_ConstantPricePolicy_ToJson() {
        // given
        PricePolicy constantPricePolicy = new ConstantPricePolicy(1000);

        // when
        String json = pricePolicyConverter.convertToDatabaseColumn(constantPricePolicy);

        // then
        assertThat(json).isEqualTo("{\"type\":\"CONSTANT\",\"variationWidth\":1000}");
    }

    @Test
    @DisplayName("올바른 JSON을 고정 할인 가격정책 객체로 변환한다")
    void transfer_Json_ToConstantPricePolicy() {
        // given
        String json = "{\"type\":\"CONSTANT\",\"variationWidth\":1000}";

        // when
        PricePolicy pricePolicy = pricePolicyConverter.convertToEntityAttribute(json);

        // then
        assertAll(
                () -> assertThat(pricePolicy.getType()).isEqualTo(PricePolicyType.CONSTANT),
                () -> assertThat(((ConstantPricePolicy) pricePolicy).getVariationWidth()).isEqualTo(1000)
        );
    }
}

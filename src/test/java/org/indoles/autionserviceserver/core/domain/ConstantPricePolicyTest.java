package org.indoles.autionserviceserver.core.domain;


import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConstantPricePolicyTest {

    @Test
    @DisplayName("경매 변동폭은 0원보다 작거나 같다면 예외가 발생한다.")
    void auction_ConstantPricePolicy_DiscountRate_Fail() {
        // expect
        assertThatThrownBy(() -> new ConstantPricePolicy(0))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A004);
    }

    @Test
    @DisplayName("할인 폭보다 가격이 더 작거나 같다면 예외가 발생한다.")
    void create_ConstantPricePolicy_InvalidDiscountRate() {
        // given
        ConstantPricePolicy constantPricePolicy = new ConstantPricePolicy(100L);

        // expect
        assertThatThrownBy(() -> constantPricePolicy.calculatePriceAtVariation(100L, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", 100L, 100L))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A008);
    }

    @Test
    @DisplayName("가격과 할인횟수가 주어지면 횟수만큼 할인이 적용된 가격을 계산한다.")
    void calculatePrice_DiscountRate() {
        // given
        ConstantPricePolicy constantPricePolicy = new ConstantPricePolicy(100L);

        // when
        long result = constantPricePolicy.calculatePriceAtVariation(10000L, 10L);

        // then
        assertThat(result).isEqualTo(9000L);
    }
}

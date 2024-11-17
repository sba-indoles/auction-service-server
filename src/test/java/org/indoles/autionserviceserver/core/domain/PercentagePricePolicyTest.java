package org.indoles.autionserviceserver.core.domain;


import org.indoles.autionserviceserver.core.auction.domain.PercentagePricePolicy;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PercentagePricePolicyTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 100})
    @DisplayName("경매 할인율은 0보다 크고 50보다 작거나 같지 않다면 예외가 발생한다.")
    void auction_PercentPricePolicy_DiscountRate_Fail(double discountRate) {
        // expect
        assertThatThrownBy(() -> new PercentagePricePolicy(discountRate))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(String.format("할인율은 0%% 초과 50%% 이하여야 합니다. 할인율: %f%%", discountRate))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 50.0})
    @DisplayName("유효한 할인율로 PercentagePricePolicy 를 생성할 수 있다")
    void create_PercentPricePolicy_Success(double discountRate) {
        // expect
        assertThatNoException().isThrownBy(() -> new PercentagePricePolicy(discountRate));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 50.1})
    @DisplayName("할인율이 0프로 이하이거나 50프로 초과이면 예외가 발생한다")
    void create_PercentPricePolicy_InvalidDiscountRate(double invalidRate) {
        // expect
        String message = String.format("할인율은 %d%% 초과 %d%% 이하여야 합니다. 할인율: %f%%", 0, 50, invalidRate);
        assertThatThrownBy(() -> new PercentagePricePolicy(invalidRate))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(message)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
    }

    @Test
    @DisplayName("가격과 할인횟수가 주어지면 횟수만큼 할인이 적용된 가격을 계산한다")
    void calculatePrice_DiscountRate() {
        // given
        PercentagePricePolicy percentagePricePolicy = new PercentagePricePolicy(10.0);
        long expected = 10000L;
        double discountFactor = (100 - 10.0) / 100.0;
        for (int i = 0; i < 10L; i++) {
            expected = (long) Math.floor(expected * discountFactor);
        }

        // when
        long result = percentagePricePolicy.calculatePriceAtVariation(10000L, 10L);

        // then
        assertThat(result).isEqualTo(expected);
    }
}

package org.indoles.autionserviceserver.entity;

import org.indoles.autionserviceserver.core.auction.domain.PercentagePricePolicy;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.core.auction.entity.enums.PricePolicyType;
import org.indoles.autionserviceserver.fixture.AuctionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuctionEntityTest {

    @Test
    @DisplayName("AuctionEntity가 정상적으로 생성되는지 테스트한다.")
    void createAuction_Success() {
        // given
        AuctionEntity auction = AuctionFixture.auctionBuild();

        // when & then
        assertThat(auction).isNotNull();
        assertEquals(auction.getSellerId(), 1L);
        assertEquals(auction.getProductName(), "Test Product");
        assertEquals(auction.getOriginPrice(), 10000L);
        assertEquals(auction.getCurrentPrice(), 9000L);
        assertEquals(auction.getOriginStock(), 100L);
        assertEquals(auction.getCurrentStock(), 50L);
        assertEquals(auction.getMaximumPurchaseLimitCount(), 5L);
        assertEquals(auction.getPricePolicy().getType(), PricePolicyType.PERCENTAGE);
        assertEquals(((PercentagePricePolicy) auction.getPricePolicy()).getPercentage(), 10.0);
        assertEquals(auction.getVariationDuration(), Duration.ofHours(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("여러 개의 AuctionEntity가 정상적으로 생성되는지 테스트한다.")
    void createAuctions_Success(int count) {
        // given
        AuctionEntity auction = AuctionFixture.auctionBuilder(count);

        // when & then
        assertThat(auction).isNotNull();
        assertEquals(auction.getSellerId(), (long) count);
        assertEquals(auction.getProductName(), "Test Product " + count);
        assertEquals(auction.getOriginPrice(), 10000L + (count * 1000));
        assertEquals(auction.getCurrentPrice(), 9000L - (count * 1000));
        assertEquals(auction.getOriginStock(), 100L);
        assertEquals(auction.getCurrentStock(), 50L);
        assertEquals(auction.getMaximumPurchaseLimitCount(), 5L);
        assertEquals(auction.getPricePolicy().getType(), PricePolicyType.PERCENTAGE);
        assertEquals(((PercentagePricePolicy) auction.getPricePolicy()).getPercentage(), 10.0);
        assertEquals(auction.getVariationDuration(), Duration.ofHours(1));
    }
}

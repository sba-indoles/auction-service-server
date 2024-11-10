package org.indoles.autionserviceserver.fixture;

import org.indoles.autionserviceserver.core.auction.domain.PercentagePricePolicy;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;

import java.time.Duration;

public class AuctionFixture {

    public static AuctionEntity auctionBuild() {
        return AuctionEntity.builder()
                .sellerId(1L)
                .productName("Test Product")
                .originPrice(10000L)
                .currentPrice(9000L)
                .originStock(100L)
                .currentStock(50L)
                .maximumPurchaseLimitCount(5L)
                .pricePolicy(new PercentagePricePolicy(10.0))
                .variationDuration(Duration.ofHours(1))
                .build();
    }

    public static AuctionEntity auctionBuilder(int count) {
        return AuctionEntity.builder()
                .sellerId((long) count)
                .productName("Test Product " + count)
                .originPrice(10000L + (count * 1000))
                .currentPrice(9000L - (count * 1000))
                .originStock(100L)
                .currentStock(50L)
                .maximumPurchaseLimitCount(5L)
                .pricePolicy(new PercentagePricePolicy(10.0))
                .variationDuration(Duration.ofHours(1))
                .build();
    }
}

package org.indoles.autionserviceserver.global;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperTest {

    @Test
    @DisplayName("경매 엔티티를 구매자 경매 간단 정보로 변환하면 도메인의 정보가 동일하게 전달된다")
    void TransferAuctionEntity_ToBuyerSimpleInfo() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Auction auction = Auction.builder()
                .id(1L)
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.minusMinutes(30))
                .finishedAt(now.plusMinutes(30))
                .isShowStock(true)
                .build();

        // when
        BuyerAuctionSimpleInfoResponse dto = Mapper.convertToBuyerAuctionSimpleInfo(auction);

        // then
        assertAll(
                () -> assertEquals(auction.getId(), dto.id()),
                () -> assertEquals(auction.getProductName(), dto.title()),
                () -> assertEquals(auction.getCurrentPrice(), dto.price()),
                () -> assertEquals(auction.getStartedAt(), dto.startedAt()),
                () -> assertEquals(auction.getFinishedAt(), dto.finishedAt())
        );
    }
}

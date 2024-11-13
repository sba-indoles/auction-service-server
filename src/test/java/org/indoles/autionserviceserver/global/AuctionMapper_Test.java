package org.indoles.autionserviceserver.global;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuctionMapper_Test {

    @Nested
    class AuctionMapper_Success {

        @Test
        @DisplayName("영속성 엔티티를 도메인 엔티티로 변환하면 정보가 동일하다")
        void transferAuctionEntityToAuction() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AuctionEntity entity = AuctionEntity.builder()
                    .id(1L)
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();

            // when
            Auction auction = Mapper.convertToAuction(entity);

            // then
            assertAll(
                    () -> assertThat(auction).isNotNull(),
                    () -> assertThat(auction.getId()).isEqualTo(1L),
                    () -> assertThat(auction.getSellerId()).isEqualTo(2L),
                    () -> assertThat(auction.getProductName()).isEqualTo("상품 이름"),
                    () -> assertThat(auction.getOriginPrice()).isEqualTo(1000L),
                    () -> assertThat(auction.getCurrentPrice()).isEqualTo(1000L),
                    () -> assertThat(auction.getOriginStock()).isEqualTo(100L),
                    () -> assertThat(auction.getCurrentStock()).isEqualTo(100L),
                    () -> assertThat(auction.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(auction.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(auction.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(auction.getStartedAt()).isEqualTo(now),
                    () -> assertThat(auction.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(auction.isShowStock()).isTrue()
            );
        }

        @Test
        @DisplayName("도메인 엔티티를 영속성 엔티티로 변환하면 정보가 동일하다")
        void transferAuctionToAuctionEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .id(1L)
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();

            // when
            AuctionEntity entity = Mapper.convertToAuctionEntity(auction);

            // then
            assertAll(
                    () -> assertThat(entity.getId()).isEqualTo(1L),
                    () -> assertThat(entity.getSellerId()).isEqualTo(2L),
                    () -> assertThat(entity.getProductName()).isEqualTo("상품 이름"),
                    () -> assertThat(entity.getOriginPrice()).isEqualTo(1000L),
                    () -> assertThat(entity.getCurrentPrice()).isEqualTo(1000L),
                    () -> assertThat(entity.getOriginStock()).isEqualTo(100L),
                    () -> assertThat(entity.getCurrentStock()).isEqualTo(100L),
                    () -> assertThat(entity.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(entity.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(entity.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(entity.getStartedAt()).isEqualTo(now),
                    () -> assertThat(entity.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(entity.isShowStock()).isTrue()
            );
        }
    }
}

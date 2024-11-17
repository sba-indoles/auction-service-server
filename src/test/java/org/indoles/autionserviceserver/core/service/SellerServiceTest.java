package org.indoles.autionserviceserver.core.service;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.core.auction.dto.Request.CreateAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.SignInInfoResponse;
import org.indoles.autionserviceserver.core.context.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SellerServiceTest extends ServiceTest {

    @Nested
    class createAuction_Method {

        @Nested
        class createAuction_Success {

            @Test
            @DisplayName("정상적으로 경매가 생성된다.")
            void createAuction_Success() {
                // given
                Long sellerId = 1L;  // 판매자 정보
                String productName = "상품이름";
                long originPrice = 10000;
                long stock = 999999;  // 재고
                long maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                Duration varitationDuration = Duration.ofMinutes(10L);  // 변동 시간 단위
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                LocalDateTime startedAt = now.plusHours(1);
                LocalDateTime finishedAt = startedAt.plusHours(1);

                SignInInfoResponse sellerInfo = new SignInInfoResponse(sellerId, Role.SELLER);
                CreateAuctionRequest command = new CreateAuctionRequest(productName, originPrice, stock,
                        maximumPurchaseLimitCount, pricePolicy, varitationDuration, now, startedAt,
                        finishedAt,
                        true
                );

                // when
                sellerService.createAuction(sellerInfo, command);
                Auction createdAuction = auctionCoreRepository.findById(1L).get();

                // then
                assertAll(
                        () -> assertThat(createdAuction.getSellerId()).isEqualTo(sellerId),
                        () -> assertThat(createdAuction.getProductName()).isEqualTo(productName),
                        () -> assertThat(createdAuction.getOriginPrice()).isEqualTo(originPrice),
                        () -> assertThat(createdAuction.getCurrentPrice()).isEqualTo(originPrice),
                        () -> assertThat(createdAuction.getOriginStock()).isEqualTo(stock),
                        () -> assertThat(createdAuction.getCurrentStock()).isEqualTo(stock),
                        () -> assertThat(createdAuction.getMaximumPurchaseLimitCount()).isEqualTo(
                                maximumPurchaseLimitCount),
                        () -> assertThat(createdAuction.getPricePolicy()).isEqualTo(pricePolicy),
                        () -> assertThat(createdAuction.getVariationDuration()).isEqualTo(varitationDuration),
                        () -> assertThat(createdAuction.getStartedAt()).isEqualTo(startedAt),
                        () -> assertThat(createdAuction.getFinishedAt()).isEqualTo(finishedAt),
                        () -> assertThat(createdAuction.isShowStock()).isTrue(),
                        () -> assertThat(createdAuction.getId()).isNotNull()
                );
            }
        }

        @ParameterizedTest
        @ValueSource(ints = {20, 30, 40, 50, 60})
        @DisplayName("경매 시간이 지속 시간이 60분 이하이면 경매가 생성된다.")
        void createAuctionWithTimeLimit_Success(int durationTime) {
            // given
            Long sellerId = 1L;
            String productName = "상품이름";
            int originPrice = 10000;
            int stock = 999999;
            int maximumPurchaseLimitCount = 10;

            int variationWidth = 1000;
            Duration varitationDuration = Duration.ofMinutes(10L);
            PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

            LocalDateTime startedAt = now.plusHours(1);
            LocalDateTime finishedAt = startedAt.plusMinutes(durationTime);

            SignInInfoResponse sellerInfo = new SignInInfoResponse(sellerId, Role.SELLER);
            CreateAuctionRequest command = new CreateAuctionRequest(
                    productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                    varitationDuration, now, startedAt, finishedAt, true
            );

            // expect
            assertThatNoException().isThrownBy(() -> sellerService.createAuction(sellerInfo, command));
        }
    }
}

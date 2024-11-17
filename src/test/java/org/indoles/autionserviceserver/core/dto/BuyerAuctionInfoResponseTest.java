package org.indoles.autionserviceserver.core.dto;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionInfoResponse;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class BuyerAuctionInfoResponseTest {
    static Stream<Arguments> auctionInfoDtoArguments() {
        return Stream.of(
                Arguments.of("상품 이름은 비어있을 수 없습니다.",
                        ErrorCode.A001, 1L, 1L, "", 10000, 10000, 10000, 10, 10, Duration.ofMinutes(1L),
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                Arguments.of("상품 원가는 0보다 커야 합니다. 상품 원가: 0",
                        ErrorCode.A002, 1L, 1L, "상품이름", 0, 10000, 10000, 10, 10, Duration.ofMinutes(1L),
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                Arguments.of("현재 가격은 0보다 커야 합니다. 현재 가격: 0",
                        ErrorCode.A011, 1L, 1L, "상품이름", 10000, 0, 10000, 10, 10, Duration.ofMinutes(1L),
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                Arguments.of("최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: 0",
                        ErrorCode.A003, 1L, 1L, "상품이름", 10000, 10000, 10000, 10, 0, Duration.ofMinutes(1L),
                        LocalDateTime.now(), LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("경매 정보 생성 요청을 정상적으로 처리한다")
    void createAuctionInfoRequest_Success() {
        // given
        Long auctionId = 1L;
        Long sellerId = 1L;
        String productName = "상품이름";
        long originPrice = 10000;
        long currentPrice = 10000;
        long originStock = 10;
        long currentStock = 10;
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        LocalDateTime startedAt = LocalDateTime.now().minusHours(1L);
        LocalDateTime finishedAt = LocalDateTime.now();

        // when
        BuyerAuctionInfoResponse buyerAuctionInfoResponse = new BuyerAuctionInfoResponse(auctionId, sellerId, productName, originPrice,
                currentPrice, originStock, currentStock,
                maximumPurchaseLimitCount, pricePolicy, varitationDuration, startedAt, finishedAt);

        // then
        assertAll(
                () -> assertThat(buyerAuctionInfoResponse.auctionId()).isEqualTo(auctionId),
                () -> assertThat(buyerAuctionInfoResponse.sellerId()).isEqualTo(sellerId),
                () -> assertThat(buyerAuctionInfoResponse.productName()).isEqualTo(productName),
                () -> assertThat(buyerAuctionInfoResponse.originPrice()).isEqualTo(originPrice),
                () -> assertThat(buyerAuctionInfoResponse.currentPrice()).isEqualTo(currentPrice),
                () -> assertThat(buyerAuctionInfoResponse.originStock()).isEqualTo(originStock),
                () -> assertThat(buyerAuctionInfoResponse.currentStock()).isEqualTo(currentStock),
                () -> assertThat(buyerAuctionInfoResponse.maximumPurchaseLimitCount()).isEqualTo(maximumPurchaseLimitCount),
                () -> assertThat(buyerAuctionInfoResponse.pricePolicy()).isEqualTo(pricePolicy),
                () -> assertThat(buyerAuctionInfoResponse.variationDuration()).isEqualTo(varitationDuration),
                () -> assertThat(buyerAuctionInfoResponse.startedAt()).isEqualTo(startedAt),
                () -> assertThat(buyerAuctionInfoResponse.finishedAt()).isEqualTo(finishedAt)
        );
    }

    @Test
    @DisplayName("재고 노출이 비활성화 되어있을 때, 현재 재고는 null이다")
    void stockIsNull_WhenStockIsNotShowed() {
        // given
        Long auctionId = 1L;
        Long sellerId = 1L;
        String productName = "상품이름";
        long originPrice = 10000;
        int stock = 10;
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(10L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        LocalDateTime startedAt = LocalDateTime.now().minusHours(1L);
        LocalDateTime finishedAt = startedAt.plusHours(1L);

        Auction auction = Auction.builder()
                .id(auctionId)
                .sellerId(sellerId)
                .productName(productName)
                .currentPrice(originPrice)
                .originPrice(originPrice)
                .originStock(stock)
                .currentStock(stock)
                .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                .pricePolicy(pricePolicy)
                .variationDuration(varitationDuration)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .isShowStock(false)
                .build();

        // when
        BuyerAuctionInfoResponse buyerAuctionInfoResponse = Mapper.convertToBuyerAuctionInfo(auction);

        // then
        assertThat(buyerAuctionInfoResponse.currentStock()).isEqualTo(null);
    }


    @ParameterizedTest
    @MethodSource("auctionInfoDtoArguments")
    @DisplayName("경매 정보 생성 요청이 잘못된 경우 예외가 발생한다")
    void createAuctionInfoRequest_ThrowsException(
            String expectedMessage,
            ErrorCode expectedErrorCode,
            Long auctionId,
            Long sellerId,
            String productName,
            long originPrice,
            long currentPrice,
            long originStock,
            long currentStock,
            int maximumPurchaseLimitCount,
            Duration variationDuration,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        // expect
        assertThatThrownBy(() -> BuyerAuctionInfoResponse.builder()
                .auctionId(auctionId)
                .sellerId(sellerId)
                .productName(productName)
                .originPrice(originPrice)
                .currentPrice(currentPrice)
                .originStock(originStock)
                .currentStock(currentStock)
                .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                .pricePolicy(PricePolicy.createConstantPricePolicy(1000))
                .variationDuration(variationDuration)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .build()
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(expectedMessage)
                .satisfies(
                        exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode));
    }
}

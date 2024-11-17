package org.indoles.autionserviceserver.core.domain;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PercentagePricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.core.fixture.AuctionFixture;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.SuccessfulOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

class AuctionTest {

    @Nested
    class createAuction_Method {

        @Nested
        @DisplayName("고정 가격 변동 정책을 이용하는 경우")
        class ConstantPricePolicyTest {

            @Test
            @DisplayName("하락하는 가격의 최소값이 0원 이하가 되지 않는 경우 경매가 정상 생성된다")
            void createAuction_Success() {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(30);
                Duration variationDuration = Duration.ofMinutes(10);
                long initialPrice = 1000L;

                // expect
                assertThatNoException().isThrownBy(
                        () -> Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build());
            }

            @Test
            @DisplayName("가격 변동폭이 가격보다 작거나 같으면 예외가 발생한다")
            void createAuction_Fail() {
                // given
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 10000;
                Duration varitationDuration = Duration.ofMinutes(1L);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);
                LocalDateTime now = LocalDateTime.now();

                // expect
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("상품이름")
                                .originPrice(originPrice)
                                .currentPrice(originPrice)
                                .originStock(stock)
                                .currentStock(stock)
                                .pricePolicy(pricePolicy)
                                .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                                .variationDuration(varitationDuration)
                                .startedAt(now.minusHours(1L))
                                .finishedAt(now)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A008);
            }

            @Test
            @DisplayName("경매 진행 중 가격이 0원 이하가 되는 경우 예외가 발생한다")
            void auctionPrice_ZeroOrLessThanZero_ExceptionThrown() {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(60);
                Duration variationDuration = Duration.ofMinutes(10);
                long initialPrice = 500;

                // expect
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: 500, 할인횟수: 5, 모든 할인 적용 후 가격: 0")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
            }

            @ParameterizedTest
            @CsvSource({
                    "1000, 60, 10, true",
                    "500, 60, 10, false",
                    "1000, 30, 10, true",
                    "300, 30, 5, false",
            })
            @DisplayName("다양한 시나리오에서 가격 검증이 올바르게 동작해야 한다")
            void validConstantPricePolicy_Success(
                    long initialPrice, long durationMinutes, long variationMinutes, boolean shouldPass) {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(durationMinutes);
                Duration variationDuration = Duration.ofMinutes(variationMinutes);

                // expect
                if (shouldPass) {
                    assertThatNoException().isThrownBy(
                            () -> Auction.builder()
                                    .sellerId(1L)
                                    .productName("productName")
                                    .originPrice(initialPrice)
                                    .currentPrice(initialPrice)
                                    .originStock(100L)
                                    .currentStock(100L)
                                    .maximumPurchaseLimitCount(10L)
                                    .pricePolicy(pricePolicy)
                                    .variationDuration(variationDuration)
                                    .startedAt(startedAt)
                                    .finishedAt(finishedAt)
                                    .isShowStock(true)
                                    .build());
                    return;
                }
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format(
                                "경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                                initialPrice,
                                durationMinutes / variationMinutes - 1,
                                initialPrice - (durationMinutes / variationMinutes - 1) * 100))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
            }
        }

        @Nested
        class PercentPricePolicyTest {

            @Test
            @DisplayName("하락하는 가격의 최소값이 0원 이하가 되지 않는 경우 경매가 정상 생성된다")
            void createAuction_Success() {
                // given
                PercentagePricePolicy pricePolicy = new PercentagePricePolicy(50.0);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(10);
                Duration variationDuration = Duration.ofMinutes(1);
                long initialPrice = 512;

                // expect
                assertThatNoException().isThrownBy(
                        () -> Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build());
            }

            @Test
            @DisplayName("경매 진행 중 가격이 0원 이하가 되는 경우 예외가 발생한다")
            void auctionPrice_ZeroOrLessThanZero_ExceptionThrown() {
                // given
                PercentagePricePolicy pricePolicy = new PercentagePricePolicy(50.0);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(10);
                Duration variationDuration = Duration.ofMinutes(1);
                long initialPrice = 256;

                // expect
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format(
                                "경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                                initialPrice,
                                9,
                                0))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
            }
        }

        @Nested
        class AuctionTime_IsNotNotValid {
            @ParameterizedTest
            @ValueSource(ints = {11, 7, 13, 21, 31, 14})
            @DisplayName("경매 시간이 나누어 떨어지지 않는 경우 예외가 발생한다")
            void auctionTime_Valid_Fail(int invalidVariationDuration) {
                // given
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 10000;
                Duration varitationDuration = Duration.ofSeconds(invalidVariationDuration);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);
                LocalDateTime now = LocalDateTime.now();

                // expect
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("상품이름")
                                .originPrice(originPrice)
                                .currentPrice(originPrice)
                                .originStock(stock)
                                .currentStock(stock)
                                .pricePolicy(pricePolicy)
                                .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                                .variationDuration(varitationDuration)
                                .startedAt(now)
                                .finishedAt(now.plusMinutes(60L))
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
            }
        }

        @Nested
        @DisplayName("경매 주기 시간이 60분을 넘긴다면")
        class AuctionTime_IsOver60Minutes {
            @Test
            @DisplayName("예외가 발생한다.")
            void Overtime_ThrowsException() {
                // given
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(60).plusNanos(1L);
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                // expect
                assertThatThrownBy(() -> Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .currentPrice(originPrice)
                        .originStock(stock)
                        .currentStock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(Duration.ofMinutes(10))
                        .startedAt(startedAt)
                        .finishedAt(finishedAt)
                        .isShowStock(true)
                        .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A007);
            }
        }

        @Nested
        @DisplayName("경매 주기 시간이 분 단위가 아니라면")
        class AuctionTime_IsNotMinute {

            @Test
            @DisplayName("예외가 발생한다.")
            void minute_throwsException() {
                // given
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(1).plusNanos(1L);
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                // expect
                assertThatThrownBy(() -> Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .currentPrice(originPrice)
                        .originStock(stock)
                        .currentStock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(Duration.ofSeconds(1L))
                        .startedAt(startedAt)
                        .finishedAt(finishedAt)
                        .isShowStock(true)
                        .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A029);
            }
        }

        @Nested
        @DisplayName("경매 주기 시간이 나누어 떨어지는 경우")
        class AuctionWithValidTime {

            @ParameterizedTest
            @ValueSource(ints = {10, 6, 12, 30, 20, 15})
            @DisplayName("경매를 정상 생성한다")
            void createAuction_Success(int validVariationDuration) {
                // given
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                Duration varitationDuration = Duration.ofSeconds(validVariationDuration);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);
                LocalDateTime now = LocalDateTime.now();

                // expect
                assertThatNoException().isThrownBy(() -> Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .currentPrice(originPrice)
                        .originStock(stock)
                        .currentStock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(varitationDuration)
                        .startedAt(now)
                        .finishedAt(now.plusMinutes(1L))
                        .isShowStock(true)
                        .build());
            }
        }
    }

    @Nested
    @DisplayName("환불 메소드는")
    class refundStock_Method {

        @Test
        @DisplayName("정상적인 요청이 오면 성공한다.")
        void refundStock_Success() {
            // given
            long originStock = 999999L;
            long currentStock = 999998L;
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(originStock)
                    .currentStock(currentStock)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30))
                    .finishedAt(now.plusMinutes(30))
                    .isShowStock(true)
                    .build();

            // when
            auction.refundStock(1L);

            // then
            assertThat(auction.getCurrentStock()).isEqualTo(999999L);
        }
    }

    @Nested
    @DisplayName("환불할 재고량이 0 이하라면")
    class refundStock_ISLessThanZero {

        @Test
        @DisplayName("예외가 발생한다.")
        void throwsException_IfStockIsLessThanZero() {
            // given
            long originStock = 999999L;
            long currentStock = 0L;
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(originStock)
                    .currentStock(currentStock)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30L))
                    .finishedAt(now.plusMinutes(30L))
                    .isShowStock(true)
                    .build();

            // expect
            assertThatThrownBy(() -> auction.refundStock(-1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("환불할 재고는 1보다 작을 수 없습니다. inputStock=-1")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A015));
        }
    }

    @Nested
    @DisplayName("환불 후 재고가 원래 재고보다 많다면")
    class refundStock_IsMoreThanOriginStock {

        @Test
        @DisplayName("예외가 발생한다.")
        void throwsException_IfStockIsMoreThanOriginStock() {
            // given
            long originStock = 999999L;
            long currentStock = 999999L;
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(originStock)
                    .currentStock(currentStock)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30L))
                    .finishedAt(now.plusMinutes(30L))
                    .isShowStock(true)
                    .build();

            // expect
            assertThatThrownBy(() -> auction.refundStock(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("환불 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=1")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A016));
        }
    }

    @Nested
    @DisplayName("입찰 메소드는")
    class submit_Method {

        @Test
        @DisplayName("경매 상태가 진행중이 아니라면 예외가 발생한다.")
        void submit_NotWaitingAuction_ThrowException() {
            // given
            Auction auction = AuctionFixture.createWaitingAuction();

            // expect
            assertThatThrownBy(() -> auction.submit(2000, 10, LocalDateTime.now()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: " + AuctionStatus.WAITING)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A013);
        }

        @Test
        @DisplayName("입찰 요청 시간 기준 현재 가격과 사용자가 요청한 가격이 다르다면 예외가 발생한다.")
        void submit_InvalidTimeAndPrice_ThrowException() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
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
            LocalDateTime requestTime = now.minusMinutes(30).plusMinutes(33);

            // expect
            assertThatThrownBy(() -> auction.submit(7001L, 10, requestTime))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d", 7000L, 7001L))
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A022);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 31L, 101L})
        @DisplayName("최대 구매 가능 수량보다 큰 구매 요청이 오면 예외가 발생한다.")
        void submit_InvalidQuantity_ThrowException(long requestQuantity) {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("productName")
                    .originPrice(10000L)
                    .currentPrice(10000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(30L)
                    .pricePolicy(new ConstantPricePolicy(1000L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30))
                    .finishedAt(now.plusMinutes(30))
                    .isShowStock(true)
                    .build();

            // expect
            assertThatThrownBy(() -> auction.submit(7000L, requestQuantity, now))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(String.format("구매 가능 갯수를 초과하거나 0이하의 갯수만큼 구매할 수 없습니다. 요청: %d, 인당구매제한: %d",
                            requestQuantity, auction.getMaximumPurchaseLimitCount()))
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A030);
        }

        @ParameterizedTest
        @ValueSource(longs = {11L, 12L, 30L})
        @DisplayName("현재 재고보다 요청한 수량이 많다면 예외가 발생한다.")
        void submit_InvalidStock_ThrowException(long requestQuantity) {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("productName")
                    .originPrice(10000L)
                    .currentPrice(10000L)
                    .originStock(100L)
                    .currentStock(10L)
                    .maximumPurchaseLimitCount(30L)
                    .pricePolicy(new ConstantPricePolicy(1000L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30))
                    .finishedAt(now.plusMinutes(30))
                    .isShowStock(true)
                    .build();

            // expect
            assertThatThrownBy(() -> auction.submit(7000L, requestQuantity, now))
                    .isInstanceOf(SuccessfulOperationException.class)
                    .hasMessage(String.format("재고가 부족합니다. 현재 재고: %d, 요청 구매 수량: %d", auction.getCurrentStock(),
                            requestQuantity))
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A012);
        }

        @Test
        @DisplayName("정상적인 구매 요청이 들어오면 상품의 현재 재고가 차감된다.")
        void submit_Success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("productName")
                    .originPrice(10000L)
                    .currentPrice(10000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(30L)
                    .pricePolicy(new ConstantPricePolicy(1000L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30))
                    .finishedAt(now.plusMinutes(30))
                    .isShowStock(true)
                    .build();
            LocalDateTime requestTime = now.minusMinutes(30).plusMinutes(33);

            // when
            auction.submit(7000L, 10, requestTime);

            // then
            assertThat(auction.getCurrentStock()).isEqualTo(90L);
        }
    }
}

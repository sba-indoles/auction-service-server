package org.indoles.autionserviceserver.core.dto;

import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.dto.Request.CreateAuctionRequest;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CreateAuctionRequestTest {
    static Stream<Arguments> generateInvalidCreateAuctionCommandArgs() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return Stream.of(
                Arguments.of("경매 재고는 인당 구매수량보다 작을 수 없다.", ErrorCode.A000,
                        "상품이름", 10000, 1, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("최대 구매 수량 제한은 0보다 커야한다.", ErrorCode.A003,
                        "상품이름", 10000, 999999, 0,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("변동 시간 단위는 0보다 커야한다.", ErrorCode.A005,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(0L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("경매 시작 시간은 반드시 현재 시간 이후여야 합니다.", ErrorCode.A014,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.minusHours(1L), now),
                Arguments.of("경매의 시작시간은 종료 시간보다 이전이어야한다.", ErrorCode.A014,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.minusSeconds(1L), now.plusHours(1L)),
                Arguments.of("상품 이름은 비어있을 수 없다.", ErrorCode.A001,
                        "", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("상품 원가는 0보다 커야한다.", ErrorCode.A002,
                        "상품이름", 0, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("경매 유형은 Null일 수 없다.", ErrorCode.G000,
                        "상품이름", 10000, 999999, 10,
                        null, Duration.ofMinutes(1L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("상품 이름(productName)은 Null일 수 없다.", ErrorCode.G000,
                        null, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("변동 주기(variationDuration)는 Null일 수 없다.", ErrorCode.G000,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        null, now,
                        now.plusHours(1L), now.plusHours(2L)),
                Arguments.of("현재 시간는 Null일 수 없다.", ErrorCode.G000,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        null, now.minusHours(2L), null,
                        now.minusHours(1L), now),
                Arguments.of("시작 시간(startedAt)은 Null일 수 없다.", ErrorCode.G000,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now.minusHours(2L),
                        null, now),
                Arguments.of("종료 시간(finishedAt)은 Null일 수 없다.", ErrorCode.G000,
                        "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L), now.minusHours(2L),
                        now.minusHours(1L), null)
        );
    }

    @Test
    @DisplayName("경매 생성 요청이 성공한다.")
    void createAuctionRequest_Success() {
        // given
        String productName = "상품이름";
        int originPrice = 10000;
        int stock = 999999;  // 재고
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        LocalDateTime startedAt = LocalDateTime.now().plusHours(1L);
        LocalDateTime finishedAt = startedAt.plusHours(1L);

        // expect
        assertThatNoException().isThrownBy(() -> new CreateAuctionRequest(
                productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, LocalDateTime.now(), startedAt, finishedAt, true
        ));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("generateInvalidCreateAuctionCommandArgs")
    @DisplayName("경매 생성 요청이 잘못된 경우 예외가 발생한다.")
    void createAuctionRequest_Fail_ThrowException(
            String displayName,
            ErrorCode expectedErrorCode,
            String productName,
            int originPrice,
            int stock,
            int maximumPurchaseLimitCount,
            PricePolicy auctionType,
            Duration varitationDuration,
            LocalDateTime nowAt,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        // expect
        assertThatThrownBy(
                () -> new CreateAuctionRequest(
                        productName,
                        originPrice,
                        stock,
                        maximumPurchaseLimitCount,
                        auctionType,
                        varitationDuration,
                        nowAt,
                        startedAt,
                        finishedAt,
                        true
                ))
                .isInstanceOf(BadRequestException.class)
                .satisfies(
                        exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode));
    }
}

package org.indoles.autionserviceserver.core.dto;

import org.indoles.autionserviceserver.core.auction.dto.Request.CancelAuctionRequest;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CancelAuctionRequestTest {

    static Stream<Arguments> generateInvalidCancelAuctionRequestArgs() {
        return Stream.of(
                Arguments.of("요청 시간이 Null일 수 없다.", ErrorCode.G000, null, 1L)
        );
    }

    @Test
    @DisplayName("경매 취소 요청이 성공한다.")
    void cancelAuctionRequest_Success() {
        // given
        LocalDateTime requestTime = LocalDateTime.now();
        long auctionId = 1L;

        // expect
        assertThatNoException().isThrownBy(() -> new CancelAuctionRequest(requestTime, auctionId));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("generateInvalidCancelAuctionRequestArgs")
    @DisplayName("경매 취소 요청이 잘못된 경우 예외가 발생한다.")
    void cancelAuctionRequest_Fail_ThrowException(
            String displayName,
            ErrorCode expectedErrorCode,
            LocalDateTime requestTime,
            long auctionId
    ) {
        // expect
        assertThatThrownBy(
                () -> new CancelAuctionRequest(requestTime, auctionId)
        )
                .isInstanceOf(BadRequestException.class)
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode));
    }
}




package org.indoles.autionserviceserver.core.dto;

import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuctionSearchConditionRequestTest {

    @Test
    @DisplayName("사용자가 경매목록을 조회할때 정상적으로 조회할 수 있다")
    void auctionSearchCondition_Success() {
        // given
        int size = 10;
        int offset = 0;

        // when
        AuctionSearchConditionRequest auctionSearchConditionRequest = new AuctionSearchConditionRequest(offset, size);

        // then
        assertAll(
                () -> assertThat(auctionSearchConditionRequest.size()).isEqualTo(size),
                () -> assertThat(auctionSearchConditionRequest.offset()).isEqualTo(offset)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    @DisplayName("사용자가 경매목록을 조회할때 size가 1보다 작거나 100보다 크면 예외가 발생한다")
    void auctionSearchCondition_SizeInvalid_ThrowsException(int size) {

        assertThatThrownBy(() -> new AuctionSearchConditionRequest(0, size))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.G001);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    @DisplayName("사용자가 경매목록을 조회할때 offset이 0보다 작으면 예외가 발생한다")
    void auctionSearchCondition_OffsetInvalid_ThrowsException(int offset) {

        assertThatThrownBy(() -> new AuctionSearchConditionRequest(offset, 10))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.G002);
    }
}

package org.indoles.autionserviceserver.core.dto;

import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AuctionSearchConditionRequestTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 101})
    @DisplayName("사용자가 경매목록을 조회할때 size가 1보다 작거나 100보다 크면 예외가 발생한다")
    void auctionSearchCondition_ThrowsException(int size) {
        assertThrows(BadRequestException.class, () -> new AuctionSearchConditionRequest(0, size));
    }
}

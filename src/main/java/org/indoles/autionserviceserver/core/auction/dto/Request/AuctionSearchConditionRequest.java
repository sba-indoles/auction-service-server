package org.indoles.autionserviceserver.core.auction.dto.Request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateSizeBetween;

/**
 * 경매 상품을 조회할 때의 조건
 *
 * @param offset 조회 시작 위치
 * @param size   조회 개수
 */

public record AuctionSearchConditionRequest(
        int offset,
        int size
) {
    public AuctionSearchConditionRequest {
        validateSizeBetween(1, 100, size);
    }
}

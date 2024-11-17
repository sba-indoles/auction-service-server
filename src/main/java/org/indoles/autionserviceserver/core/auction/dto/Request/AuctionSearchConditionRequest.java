package org.indoles.autionserviceserver.core.auction.dto.Request;


import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateOffset;
import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateSizeBetween;

/**
 * 경매 상품을 조회할 때의 조건
 *
 * @param offset 조회 시작 위치 (default: 0)
 * @param size   조회 개수 조회할 거래 내역의 개수 (default: 10) (Min: 1, Max: 100)
 */

public record AuctionSearchConditionRequest(
        int offset,
        int size
) {
    public AuctionSearchConditionRequest {
        validateSizeBetween(1, 100, size);
        validateOffset(offset);
    }
}

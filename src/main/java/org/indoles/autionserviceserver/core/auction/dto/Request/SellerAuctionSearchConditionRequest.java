package org.indoles.autionserviceserver.core.auction.dto.Request;

/**
 * 판매자가 경매를 조회할때의 조건
 *
 * @param sellerId 판매자 ID
 * @param offset 조회 시작 위치 (default: 0)
 * @param size   조회 개수 조회할 거래 내역의 개수 (default: 10) (Min: 1, Max: 100)
 */

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateOffset;
import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateSizeBetween;

public record SellerAuctionSearchConditionRequest(
        long sellerId,
        int offset,
        int size
) {
    public SellerAuctionSearchConditionRequest {
        validateSizeBetween(1, 100, size);
        validateOffset(offset);
    }
}

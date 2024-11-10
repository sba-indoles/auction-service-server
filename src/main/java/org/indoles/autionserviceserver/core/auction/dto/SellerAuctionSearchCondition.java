package org.indoles.autionserviceserver.core.auction.dto;

/**
 * 판매자가 경매를 조회할때의 조건
 * @param sellerId 판매자 ID
 * @param offset   조회 시작 위치
 * @param size     조회 개수
 */

import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction.validateSizeBetween;

public record SellerAuctionSearchCondition(
        long sellerId,
        int offset,
        int size
) {
    public SellerAuctionSearchCondition {
        validateSizeBetween(1, 100, size);
    }
}

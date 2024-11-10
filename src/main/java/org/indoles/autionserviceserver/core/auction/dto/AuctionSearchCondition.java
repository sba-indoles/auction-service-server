package org.indoles.autionserviceserver.core.auction.dto;

import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction.*;

/**
 * 경매 상품을 조회할 때의 조건
 * @param offset  조회 시작 위치
 * @param size    조회 개수
 */

public record AuctionSearchCondition(
        int offset,
        int size
) {
    public AuctionSearchCondition {
        validateSizeBetween(1, 100, size);
    }
}

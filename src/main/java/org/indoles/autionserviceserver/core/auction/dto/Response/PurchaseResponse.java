package org.indoles.autionserviceserver.core.auction.dto.Response;

import java.util.UUID;

/**
 * 구매 응답
 * @param purchaseId
 */

public record PurchaseResponse(
        UUID purchaseId
) {
}

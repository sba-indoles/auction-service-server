package org.indoles.autionserviceserver.core.auction.dto;

import java.util.UUID;

public record PurchaseResponse(
        UUID receiptId
) {
}


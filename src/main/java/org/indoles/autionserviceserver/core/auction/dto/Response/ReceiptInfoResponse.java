package org.indoles.autionserviceserver.core.auction.dto.Response;

import lombok.Builder;
import org.indoles.autionserviceserver.core.auction.domain.enums.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReceiptInfoResponse(
        UUID receiptId,
        String productName,
        long price,
        long quantity,
        ReceiptStatus receiptStatus,
        long auctionId,
        long sellerId,
        long buyerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

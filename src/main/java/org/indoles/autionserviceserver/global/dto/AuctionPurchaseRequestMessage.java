package org.indoles.autionserviceserver.global.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AuctionPurchaseRequestMessage(
        UUID requestId,
        Long buyerId,
        Long auctionId,
        Long price,
        Long quantity,
        LocalDateTime requestTime
) {
}

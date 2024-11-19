package org.indoles.autionserviceserver.core.auction.dto.Request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AuctionRefundRequestMessage(
        SignInfoRequest buyerInfo,
        UUID receiptId,
        LocalDateTime requestTime
) {
}


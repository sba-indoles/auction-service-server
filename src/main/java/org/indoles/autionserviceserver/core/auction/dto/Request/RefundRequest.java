package org.indoles.autionserviceserver.core.auction.dto.Request;

import lombok.Builder;

@Builder
public record RefundRequest(
        Long receiverId,
        Long amount

) {
}

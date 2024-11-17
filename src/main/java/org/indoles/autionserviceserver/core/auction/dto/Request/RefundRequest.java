package org.indoles.autionserviceserver.core.auction.dto.Request;

public record RefundRequest(
        Long receiverId,
        Long amount

) {
}

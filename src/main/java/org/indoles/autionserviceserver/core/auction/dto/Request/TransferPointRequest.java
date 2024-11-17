package org.indoles.autionserviceserver.core.auction.dto.Request;

public record TransferPointRequest(
        Long receiverId,
        Long amount
) {
}

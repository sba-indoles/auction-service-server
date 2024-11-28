package org.indoles.autionserviceserver.core.auction.dto.Request;

public record CreateReceiptRequestWrapper(
        SignInfoRequest signInfoRequest,
        CreateReceiptRequest createReceiptRequest
) {
}

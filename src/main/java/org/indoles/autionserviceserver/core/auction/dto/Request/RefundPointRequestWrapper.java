package org.indoles.autionserviceserver.core.auction.dto.Request;

public record RefundPointRequestWrapper
        (SignInfoRequest signInfoRequest,
         RefundRequest refundPointRequest
        ) {
}

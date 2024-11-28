package org.indoles.autionserviceserver.core.auction.dto.Request;

public record TransferPointRequestWrapper(
        SignInfoRequest signInfoRequest,
        TransferPointRequest transferPointRequest
) {
}


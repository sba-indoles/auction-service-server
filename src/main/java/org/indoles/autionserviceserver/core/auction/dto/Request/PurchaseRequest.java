package org.indoles.autionserviceserver.core.auction.dto.Request;

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validatePrice;
import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateQuantity;

public record PurchaseRequest(
        long price,
        long quantity
) {
    public PurchaseRequest {
        validatePrice(price);
        validateQuantity(quantity);
    }
}


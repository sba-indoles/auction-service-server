package org.indoles.autionserviceserver.core.auction.dto;

import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;
import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.*;

public record PurchaseRequest(
        long price,
        long quantity
) {
    public PurchaseRequest {
        validatePrice(price);
        validateQuantity(quantity);
    }

    private void validatePrice(long price) {
        if (price < 0) {
            throw new AuctionException(AUCTION_PRICE_MISMATCH);
        }
    }

    private void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new AuctionException(INVALID_PURCHASE_QUANTITY);
        }
    }
}


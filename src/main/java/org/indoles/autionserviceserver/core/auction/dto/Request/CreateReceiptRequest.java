package org.indoles.autionserviceserver.core.auction.dto.Request;

import lombok.Builder;
import org.indoles.autionserviceserver.core.auction.domain.enums.ReceiptStatus;

import java.util.UUID;

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.*;

@Builder
public record CreateReceiptRequest(
        UUID receiptId,
        String productName,
        long price,
        long quantity,
        ReceiptStatus receiptStatus,
        long auctionId,
        long sellerId,
        long buyerId
) {
    public CreateReceiptRequest {
        validateReceiptId(receiptId);
        validateProductName(productName);
        validatePrice(price);
        validateQuantity(quantity);
        validateReceiptStatus(receiptStatus);
        validateAuctionId(auctionId);
        validateSellerId(sellerId);
        validateBuyerId(buyerId);
    }
}

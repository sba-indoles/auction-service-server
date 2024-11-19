package org.indoles.autionserviceserver.core.auction.dto.Request;

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.*;
import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateAmount;
import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateReceiverId;

public record TransferPointRequest(
        Long receiverId,
        Long amount
) {

    public TransferPointRequest {
        validateNotNull(receiverId, "수신자 ID");
        validateReceiverId(receiverId);
        validateAmount(amount);
    }
}

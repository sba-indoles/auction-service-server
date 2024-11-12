package org.indoles.autionserviceserver.core.auction.dto;

import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.validateNotNull;


/**
 * 경매 취소을 위한 DTO
 *
 * @param requestTime 요청 시간
 * @param auctionId   취소할 경매 ID
 */

public record CancelAuctionCommand(
        LocalDateTime requestTime,
        Long auctionId
) {
    public CancelAuctionCommand {
        validateNotNull(requestTime, "requestTime");
    }

    public void validate() {
        validateNotNull(requestTime, "requestTime");
    }
}

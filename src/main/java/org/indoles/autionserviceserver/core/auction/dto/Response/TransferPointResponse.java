package org.indoles.autionserviceserver.core.auction.dto.Response;

public record TransferPointResponse(
        Long senderId,        // 송신자 ID
        Long receiverId,      // 수신자 ID
        Long amount,          // 전송된 포인트 금액
        Long remainingPoints   // 송신자의 남은 포인트
){
}

package org.indoles.autionserviceserver.core.auction.dto.Response;

public record RefundResponse(
        Long senderId,        // 송신자 ID
        Long receiverId,      // 수신자 ID
        Long amount,          // 환불된 포인트 금액
        Long remainingPoints   // 송신자의 남은 포인트
){
}

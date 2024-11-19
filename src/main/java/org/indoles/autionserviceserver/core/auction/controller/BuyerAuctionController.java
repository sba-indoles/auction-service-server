package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.Buyer;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.Login;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.PublicAccess;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.Roles;
import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.core.auction.dto.Request.*;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.PurchaseResponse;
import org.indoles.autionserviceserver.core.auction.service.BuyerService;
import org.indoles.autionserviceserver.global.dto.AuctionPurchaseRequestMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class BuyerAuctionController {

    private final BuyerService buyerService;

    /**
     * 경매 목록 조회 API - 모든 사용자 조회
     */

    @PublicAccess
    @GetMapping("/search")
    public ResponseEntity<List<BuyerAuctionSimpleInfoResponse>> getAuctions(
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "size") int size
    ) {
        AuctionSearchConditionRequest condition = new AuctionSearchConditionRequest(offset, size);
        List<BuyerAuctionSimpleInfoResponse> infos = buyerService.getBuyerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    /**
     * 경매 물품 상세 조회 API - 모든 사용자 조회
     */

    @PublicAccess
    @GetMapping("/{auctionId}")
    public ResponseEntity<BuyerAuctionInfoResponse> getAuction(
            @PathVariable(name = "auctionId") Long auctionId) {

        BuyerAuctionInfoResponse result = buyerService.getBuyerAuction(auctionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 경매 입찰 API(구매자 전용)
     */
    @Buyer
    @PostMapping("/{auctionId}/purchase")
    public ResponseEntity<PurchaseResponse> submitAuction(
            @Login SignInfoRequest signInfoRequest,
            @CurrentTime LocalDateTime now,
            @PathVariable(name = "auctionId") Long auctionId,
            @RequestBody PurchaseRequest purchaseRequest) {

        AuctionPurchaseRequestMessage requestMessage = AuctionPurchaseRequestMessage.builder()
                .requestId(UUID.randomUUID())
                .buyerId(signInfoRequest.id())
                .auctionId(auctionId)
                .price(purchaseRequest.price())
                .quantity(purchaseRequest.quantity())
                .requestTime(now)
                .build();

        buyerService.submitPurchase(requestMessage, signInfoRequest);

        PurchaseResponse response = new PurchaseResponse(requestMessage.requestId());
        return ResponseEntity.ok(response);
    }

    /**
     * 경매 입찰 취소 API(구매자 전용)
     */
    @Buyer
    @DeleteMapping("/{receiptId}/refund")
    public ResponseEntity<Void> cancelAuction(
            @Login SignInfoRequest signInfoRequest,
            @PathVariable(name = "receiptId") UUID receiptId,
            @CurrentTime LocalDateTime localDateTime) {

        var message = new AuctionRefundRequestMessage(signInfoRequest, receiptId, localDateTime);
        buyerService.cancelPurchase(message);
        return ResponseEntity.ok().build();
    }
}


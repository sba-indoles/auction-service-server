package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.BuyerOnly;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.Login;
import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.PurchaseRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.PurchaseResponse;
import org.indoles.autionserviceserver.core.auction.dto.Request.SignInfoRequest;
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
     * 경매 목록 조회 API
     */

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
     * 경매 목록 상세 조회 API(구매자 전용)
     */
    @BuyerOnly
    @GetMapping("/{auctionId}")
    public ResponseEntity<BuyerAuctionInfoResponse> getAuction(
            @Login SignInfoRequest signInfoRequest,
            @PathVariable("auctionId") Long auctionId) {

        BuyerAuctionInfoResponse result = buyerService.getBuyerAuction(auctionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 경매 입찰 API(구매자 전용)
     */
    @BuyerOnly
    @PostMapping("/{auctionId}/purchase")
    public ResponseEntity<PurchaseResponse> submitAuction(
            @Login SignInfoRequest signInfoRequest,
            @CurrentTime LocalDateTime localDateTime,
            @PathVariable(name = "auctionId") Long auctionId,
            @RequestBody PurchaseRequest purchaseRequest) {

        AuctionPurchaseRequestMessage requestMessage = AuctionPurchaseRequestMessage.builder()
                .requestId(UUID.randomUUID())
                .buyerId(signInfoRequest.id())
                .auctionId(auctionId)
                .price(purchaseRequest.price())
                .quantity(purchaseRequest.quantity())
                .requestTime(localDateTime)
                .build();

        buyerService.submitPurchase(auctionId, purchaseRequest.price(), purchaseRequest.quantity(), localDateTime, signInfoRequest);
        PurchaseResponse response = new PurchaseResponse(requestMessage.requestId());
        return ResponseEntity.ok(response);
    }

    /**
     * 경매 입찰 취소 API(구매자 전용)
     */
    @BuyerOnly
    @DeleteMapping("/{auctionId}/refund")
    public void cancelAuction(
            @Login SignInfoRequest signInfoRequest,
            @PathVariable("auctionId") Long auctionId,
            @RequestParam("quantity") Long quantity) {

        buyerService.cancelPurchase(auctionId, quantity, signInfoRequest);
    }
}


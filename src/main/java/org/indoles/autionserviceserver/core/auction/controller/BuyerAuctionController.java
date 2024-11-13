package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.SignInInfo;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.service.AuctionService;
import org.indoles.autionserviceserver.global.dto.AuctionPurchaseRequestMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class BuyerAuctionController {

    private final AuctionService auctionService;


    /**
     * 경매 목록 조회 API(구매자 전용)
     */

    @GetMapping("/auctions")
    public ResponseEntity<List<BuyerAuctionSimpleInfo>> getAuctions(@RequestParam(name = "offset") int offset,
                                                                    @RequestParam(name = "size") int size) {
        AuctionSearchCondition condition = new AuctionSearchCondition(offset, size);
        List<BuyerAuctionSimpleInfo> infos = auctionService.getBuyerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    /**
     * 경매 목록 상세 조회 API(구매자 전용)
     */
    @GetMapping("/auctions/{auctionId}")
    public ResponseEntity<BuyerAuctionInfo> getAuction(@PathVariable("auctionId") Long auctionId) {
        BuyerAuctionInfo result = auctionService.getBuyerAuction(auctionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 경매 입찰 API(구매자 전용)
     */
    @PostMapping("/auctions/{auctionId}/purchase")
    public ResponseEntity<PurchaseResponse> submitAuction(SignInInfo signInInfo,
                                                          @CurrentTime LocalDateTime now,
                                                          @PathVariable(name = "auctionId") Long auctionId,
                                                          @RequestBody PurchaseRequest purchaseRequest) {
        AuctionPurchaseRequestMessage requestMessage = AuctionPurchaseRequestMessage.builder()
                .requestId(UUID.randomUUID())
                .buyerId(signInInfo.id())
                .auctionId(auctionId)
                .price(purchaseRequest.price())
                .quantity(purchaseRequest.quantity())
                .requestTime(now)
                .build();

        PurchaseResponse response = new PurchaseResponse(requestMessage.requestId());
        return ResponseEntity.ok(response);
    }
}


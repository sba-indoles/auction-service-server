package org.indoles.autionserviceserver.core.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.BuyerOnly;
import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.service.AuctionService;
import org.indoles.autionserviceserver.global.dto.AuctionPurchaseRequestMessage;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.InfraStructureException;
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
    private final ObjectMapper objectMapper;

    /**
     * 경매 목록 조회 API(구매자 전용)
     */

    @GetMapping
    public ResponseEntity<List<BuyerAuctionSimpleInfo>> getAuctions(@RequestParam(name = "offset") int offset,
                                                                    @RequestParam(name = "size") int size) {
        AuctionSearchCondition condition = new AuctionSearchCondition(offset, size);
        List<BuyerAuctionSimpleInfo> infos = auctionService.getBuyerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    /**
     * 경매 목록 상세 조회 API(구매자 전용)
     */
    @BuyerOnly
    @GetMapping("/{auctionId}")
    public ResponseEntity<BuyerAuctionInfo> getAuction(@PathVariable("auctionId") Long auctionId) {
        BuyerAuctionInfo result = auctionService.getBuyerAuction(auctionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 경매 입찰 API(구매자 전용)
     */
    @BuyerOnly
    @PostMapping("/{auctionId}/purchase")
    public ResponseEntity<PurchaseResponse> submitAuction(
            @RequestHeader("X-SignIn-Info") String signInInfoString,
            @CurrentTime LocalDateTime now,
            @PathVariable(name = "auctionId") Long auctionId,
            @RequestBody PurchaseRequest purchaseRequest) {

        SignInInfo signInInfo = convertToSignInInfo(signInInfoString);

        // 입찰 요청 처리
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

    private SignInInfo convertToSignInInfo(String signInInfoString) {
        try {
            return objectMapper.readValue(signInInfoString, SignInInfo.class);
        } catch (Exception e) {
            throw new InfraStructureException("SignInfo 변환 실패" + e, ErrorCode.A031);
        }
    }
}


package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.BuyerOnly;
import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.service.AuctionService;
import org.indoles.autionserviceserver.global.dto.AuctionPurchaseRequestMessage;
import org.indoles.autionserviceserver.global.exception.AuthorizationException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.InfraStructureException;
import org.indoles.autionserviceserver.global.util.JwtTokenProvider;
import org.springframework.http.HttpStatus;
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

    private final AuctionService auctionService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 경매 목록 조회 API
     */

    @GetMapping("/search")
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
    public ResponseEntity<BuyerAuctionInfo> getAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("auctionId") Long auctionId) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                jwtTokenProvider.getSignInInfoFromToken(token);
                BuyerAuctionInfo result = auctionService.getBuyerAuction(auctionId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
        }
    }

    /**
     * 경매 입찰 API(구매자 전용)
     */
    @BuyerOnly
    @PostMapping("/{auctionId}/purchase")
    public ResponseEntity<PurchaseResponse> submitAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @CurrentTime LocalDateTime now,
            @PathVariable(name = "auctionId") Long auctionId,
            @RequestBody PurchaseRequest purchaseRequest) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfo signInInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                AuctionPurchaseRequestMessage requestMessage = AuctionPurchaseRequestMessage.builder()
                        .requestId(UUID.randomUUID())
                        .buyerId(signInInfo.id())
                        .auctionId(auctionId)
                        .price(purchaseRequest.price())
                        .quantity(purchaseRequest.quantity())
                        .requestTime(now)
                        .build();
                auctionService.submitPurchase(auctionId, purchaseRequest.price(), purchaseRequest.quantity(), now);
                PurchaseResponse response = new PurchaseResponse(requestMessage.requestId());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
        }
    }

    /**
     * 경매 입찰 취소 API(구매자 전용)
     */
    @BuyerOnly
    @DeleteMapping("/{auctionId}/refund")
    public void cancelAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("auctionId") Long auctionId,
            @RequestParam("quantity") Long quantity,
            @CurrentTime LocalDateTime now
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                jwtTokenProvider.getSignInInfoFromToken(token);
                auctionService.cancelPurchase(auctionId, quantity);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                throw new InfraStructureException("서버 에러", ErrorCode.A001);
            }
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
        }
    }
}


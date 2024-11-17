package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.BuyerOnly;
import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.PurchaseRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.BuyerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.PurchaseResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.SignInInfoResponse;
import org.indoles.autionserviceserver.core.auction.service.BuyerService;
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

    private final BuyerService buyerService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 경매 목록 조회 API
     */

    @GetMapping("/search")
    public ResponseEntity<List<BuyerAuctionSimpleInfoResponse>> getAuctions(@RequestParam(name = "offset") int offset,
                                                                            @RequestParam(name = "size") int size) {
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
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("auctionId") Long auctionId) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                jwtTokenProvider.getSignInInfoFromToken(token);
                BuyerAuctionInfoResponse result = buyerService.getBuyerAuction(auctionId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
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
                SignInInfoResponse signInInfoResponse = jwtTokenProvider.getSignInInfoFromToken(token);
                AuctionPurchaseRequestMessage requestMessage = AuctionPurchaseRequestMessage.builder()
                        .requestId(UUID.randomUUID())
                        .buyerId(signInInfoResponse.id())
                        .auctionId(auctionId)
                        .price(purchaseRequest.price())
                        .quantity(purchaseRequest.quantity())
                        .requestTime(now)
                        .build();

                buyerService.submitPurchase(auctionId, purchaseRequest.price(), purchaseRequest.quantity(), now, authorizationHeader);
                PurchaseResponse response = new PurchaseResponse(requestMessage.requestId());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error submit Auction: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
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
            @RequestParam("quantity") Long quantity
            //@CurrentTime LocalDateTime now
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                jwtTokenProvider.getSignInInfoFromToken(token);
                buyerService.cancelPurchase(auctionId, quantity, authorizationHeader);
            } catch (Exception e) {
                log.error("Error cancel Auction: {}", e.getMessage());
                throw new InfraStructureException("서버 에러", ErrorCode.AU02);
            }
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
        }
    }
}


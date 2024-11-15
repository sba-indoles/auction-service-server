package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.SellerOnly;
import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.service.AuctionService;
import org.indoles.autionserviceserver.global.exception.AuthorizationException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.util.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class SellerAuctionController {

    private final AuctionService auctionService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 경매 등록 API(판매자 전용)
     */

    @SellerOnly
    @PostMapping
    public ResponseEntity<Void> createAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateAuctionCommand request,
            @CurrentTime LocalDateTime now
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfo signInInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                CreateAuctionCommand command = new CreateAuctionCommand(
                        request.productName(),
                        request.originPrice(),
                        request.stock(),
                        request.maximumPurchaseLimitCount(),
                        request.pricePolicy(),
                        request.variationDuration(),
                        now,
                        request.startedAt(),
                        request.finishedAt(),
                        request.isShowStock()
                );
                auctionService.createAuction(signInInfo, command);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 경매 취소 API(판매자 전용)
     */
    @SellerOnly
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("auctionId") Long auctionId,
            @CurrentTime LocalDateTime now
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfo signInInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                CancelAuctionCommand command = new CancelAuctionCommand(now, auctionId);
                auctionService.cancelAuction(signInInfo, command);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
        }
    }

    /**
     * 경매 조회 API(판매자 전용)
     */
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfo>> getSellerAuctions(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "size") int size
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfo signInInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(signInInfo.id(), offset, size);
                List<SellerAuctionSimpleInfo> infos = auctionService.getSellerAuctionSimpleInfos(condition);
                return ResponseEntity.ok(infos);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
        }
    }

    /**
     * 경매 상세 조회 API(판매자 전용)
     */

    @SellerOnly
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfo> getSellerAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("auctionId") Long auctionId
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfo signInInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                SellerAuctionInfo info = auctionService.getSellerAuction(signInInfo, auctionId);
                return ResponseEntity.ok(info);
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.A001);
        }
    }
}

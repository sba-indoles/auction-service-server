package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.SellerOnly;
import org.indoles.autionserviceserver.core.auction.dto.Request.CancelAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.CreateAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.SellerAuctionInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.SellerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.SignInInfoResponse;
import org.indoles.autionserviceserver.core.auction.service.SellerService;
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

    private final SellerService sellerService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 경매 등록 API(판매자 전용)
     */

    @SellerOnly
    @PostMapping
    public ResponseEntity<Void> createAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateAuctionRequest request,
            @CurrentTime LocalDateTime now
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfoResponse signInInfoResponse = jwtTokenProvider.getSignInInfoFromToken(token);
                CreateAuctionRequest command = new CreateAuctionRequest(
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
                sellerService.createAuction(signInInfoResponse, command);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Error creating auction: {}", e.getMessage());
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
                SignInInfoResponse signInInfoResponse = jwtTokenProvider.getSignInInfoFromToken(token);
                CancelAuctionRequest command = new CancelAuctionRequest(now, auctionId);
                sellerService.cancelAuction(signInInfoResponse, command);
            } catch (Exception e) {
                log.error("Error cancel auction: {}", e.getMessage());
                throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU01);
            }
        }else {
            log.error("Unauthorized: JWT validation failed");
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
        }
    }

    /**
     * 경매 조회 API(판매자 전용)
     */
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfoResponse>> getSellerAuctions(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "size") int size
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfoResponse signInInfoResponse = jwtTokenProvider.getSignInInfoFromToken(token);
                SellerAuctionSearchConditionRequest condition = new SellerAuctionSearchConditionRequest(signInInfoResponse.id(), offset, size);
                List<SellerAuctionSimpleInfoResponse> infos = sellerService.getSellerAuctionSimpleInfos(condition);
                return ResponseEntity.ok(infos);
            } catch (Exception e) {
                log.error("Error creating auction: {}", e.getMessage());
                throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU01);
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
        }
    }

    /**
     * 경매 상세 조회 API(판매자 전용)
     */

    @SellerOnly
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfoResponse> getSellerAuction(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("auctionId") Long auctionId
    ) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfoResponse signInInfoResponse = jwtTokenProvider.getSignInInfoFromToken(token);
                SellerAuctionInfoResponse info = sellerService.getSellerAuction(signInInfoResponse, auctionId);
                return ResponseEntity.ok(info);
            } catch (Exception e) {
                log.error("Error searching auction: {}", e.getMessage());
                throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU01);
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
        }
    }
}

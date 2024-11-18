package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.Login;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.SellerOnly;
import org.indoles.autionserviceserver.core.auction.dto.Request.CancelAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.CreateAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.SellerAuctionInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.SellerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Request.SignInfoRequest;
import org.indoles.autionserviceserver.core.auction.service.SellerService;
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

    /**
     * 경매 등록 API(판매자 전용)
     */

    @SellerOnly
    @PostMapping
    public ResponseEntity<Void> createAuction(
            @Login SignInfoRequest signInfoRequest,
            @RequestBody CreateAuctionRequest request,
            @CurrentTime LocalDateTime localDateTime
    ) {

        CreateAuctionRequest command = new CreateAuctionRequest(
                request.productName(),
                request.originPrice(),
                request.stock(),
                request.maximumPurchaseLimitCount(),
                request.pricePolicy(),
                request.variationDuration(),
                request.requestTime(),
                request.startedAt(),
                request.finishedAt(),
                request.isShowStock()
        );

        sellerService.createAuction(signInfoRequest, command);
        return ResponseEntity.ok().build();
    }

    /**
     * 경매 취소 API(판매자 전용)
     */
    @SellerOnly
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(
            @Login SignInfoRequest signInfoRequest,
            @PathVariable("auctionId") Long auctionId,
            @CurrentTime LocalDateTime localDateTime
    ) {
        CancelAuctionRequest command = new CancelAuctionRequest(localDateTime, auctionId);
        sellerService.cancelAuction(signInfoRequest, command);
    }

    /**
     * 경매 조회 API(판매자 전용)
     */
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfoResponse>> getSellerAuctions(
            @Login SignInfoRequest signInfoRequest,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "size") int size
    ) {

        SellerAuctionSearchConditionRequest condition = new SellerAuctionSearchConditionRequest(signInfoRequest.id(), offset, size);
        List<SellerAuctionSimpleInfoResponse> infos = sellerService.getSellerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    /**
     * 경매 상세 조회 API(판매자 전용)
     */

    @SellerOnly
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfoResponse> getSellerAuction(
            @Login SignInfoRequest signInfoRequest,
            @PathVariable("auctionId") Long auctionId
    ) {
        SellerAuctionInfoResponse info = sellerService.getSellerAuction(signInfoRequest, auctionId);
        return ResponseEntity.ok(info);
    }
}

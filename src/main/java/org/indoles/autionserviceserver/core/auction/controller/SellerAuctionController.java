package org.indoles.autionserviceserver.core.auction.controller;

import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.MemberServiceClient;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.SellerOnly;
import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class SellerAuctionController {

    private final AuctionService auctionService;
    private final MemberServiceClient memberServiceClient;


    /**
     * 경매 등록 API(판매자 전용)
     */

    @SellerOnly
    @PostMapping
    public ResponseEntity<Void> createAuction(SignInInfo sellerInfo,
                                              @RequestBody CreateAuctionCommand request,
                                              @CurrentTime LocalDateTime now) {

        SignInInfo signInInfo = memberServiceClient.getSignInInfo();

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
        auctionService.createAuction(sellerInfo, command);
        return ResponseEntity.ok().build();
    }

    /**
     * 경매 취소 API(판매자 전용)
     */
    @SellerOnly
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(SignInInfo sellerInfo,
                              @PathVariable("auctionId") Long auctionId,
                              @CurrentTime LocalDateTime now) {

        SignInInfo signInInfo = memberServiceClient.getSignInInfo();
        CancelAuctionCommand command = new CancelAuctionCommand(now, auctionId);
        auctionService.cancelAuction(sellerInfo, command);
    }

    /**
     * 경매 조회 API(판매자 전용)
     */
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfo>> getSellerAuctions(SignInInfo sellerInfo,
                                                                           @RequestParam(name = "offset") int offset,
                                                                           @RequestParam(name = "size") int size) {
        SignInInfo signInInfo = memberServiceClient.getSignInInfo();

        SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(sellerInfo.id(), offset, size);
        List<SellerAuctionSimpleInfo> infos = auctionService.getSellerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    /**
     * 경매 상세 조회 API(판매자 전용)
     */

    @SellerOnly
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfo> getSellerAuction(SignInInfo sellerInfo,
                                                              @PathVariable("auctionId") Long auctionId) {

        SignInInfo signInInfo = memberServiceClient.getSignInInfo();

        SellerAuctionInfo info = auctionService.getSellerAuction(sellerInfo, auctionId);
        return ResponseEntity.ok(info);
    }
}

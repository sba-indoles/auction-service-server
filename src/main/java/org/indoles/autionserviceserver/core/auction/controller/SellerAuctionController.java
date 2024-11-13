package org.indoles.autionserviceserver.core.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTime;
import org.indoles.autionserviceserver.core.auction.controller.interfaces.SellerOnly;
import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.service.AuctionService;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.InfraStructureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class SellerAuctionController {

    private final AuctionService auctionService;
    private final ObjectMapper objectMapper;


    /**
     * 경매 등록 API(판매자 전용)
     */

    @SellerOnly
    @PostMapping
    public ResponseEntity<Void> createAuction(
            @RequestHeader("X-SignIn-Info") String sellerInfo,
            @RequestBody CreateAuctionCommand request,
            @CurrentTime LocalDateTime now
    ) {

        SignInInfo signInInfo = convertToSignInInfo(sellerInfo);

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
    }

    /**
     * 경매 취소 API(판매자 전용)
     */
    @SellerOnly
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(
            @RequestHeader("X-SignIn-Info") String sellerInfo,
            @PathVariable("auctionId") Long auctionId,
            @CurrentTime LocalDateTime now
    ) {

        SignInInfo signInInfo = convertToSignInInfo(sellerInfo);
        CancelAuctionCommand command = new CancelAuctionCommand(now, auctionId);
        auctionService.cancelAuction(signInInfo, command);
    }

    /**
     * 경매 조회 API(판매자 전용)
     */
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfo>> getSellerAuctions(
            @RequestHeader("X-SignIn-Info") String sellerInfo,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "size") int size
    ) {
        SignInInfo signInInfo = convertToSignInInfo(sellerInfo);

        SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(signInInfo.id(), offset, size);
        List<SellerAuctionSimpleInfo> infos = auctionService.getSellerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    /**
     * 경매 상세 조회 API(판매자 전용)
     */

    @SellerOnly
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfo> getSellerAuction(
            @RequestHeader("X-SignIn-Info") String sellerInfo,
            @PathVariable("auctionId") Long auctionId
    ) {

        SignInInfo signInInfo = convertToSignInInfo(sellerInfo);

        SellerAuctionInfo info = auctionService.getSellerAuction(signInInfo, auctionId);
        return ResponseEntity.ok(info);
    }

    private SignInInfo convertToSignInInfo(String signInInfoString) {
        try {
            return objectMapper.readValue(signInInfoString, SignInInfo.class);
        } catch (Exception e) {
            throw new InfraStructureException("SignInfo 변환 실패" + e, ErrorCode.A031);
        }
    }
}

package org.indoles.autionserviceserver.core.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.core.auction.dto.Request.*;
import org.indoles.autionserviceserver.core.auction.dto.Response.*;
import org.indoles.autionserviceserver.core.auction.infra.AuctionCoreRepository;
import org.indoles.autionserviceserver.core.auction.utils.MemberFeignClient;
import org.indoles.autionserviceserver.global.dto.AuctionPurchaseRequestMessage;
import org.indoles.autionserviceserver.global.exception.AuthorizationException;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.NotFoundException;
import org.indoles.autionserviceserver.global.util.JwtTokenProvider;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BuyerService {

    private final AuctionCoreRepository auctionCoreRepository;
    private final MemberFeignClient memberFeignClient;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 경매 상품에 대한 입찰(구매)을 진행하는 서비스 로직
     *
     * @param message return 구매자용 경매 정보
     */

    @Transactional
    public void submitPurchase(AuctionPurchaseRequestMessage message, SignInfoRequest buyerInfo) {
        Auction auction = findAuctionObject(message.auctionId());
        auction.submit(message.price(), message.quantity(), message.requestTime());

        long buyerId = buyerInfo.id();
        long sellerId = auction.getSellerId();
        long totalAmount = message.price() * message.quantity();

        TransferPointRequest transferRequest = new TransferPointRequest(sellerId, totalAmount);

        String token = jwtTokenProvider.createAccessToken(buyerInfo);

        TransferPointResponse transferResponse = memberFeignClient.pointTransfer("Bearer " + token, transferRequest);

        if (transferResponse == null || transferResponse.remainingPoints() < 0) {
            log.error("포인트 전송 실패: {}", transferRequest);
            throw new RuntimeException("포인트 전송 실패. 판매자 ID: " + sellerId + ", 구매자 ID: " + buyerId);
        }

        //거래 내역 서버 기록 - 거래 내역 서버
    }

    private Auction findAuctionObject(long auctionId) {
        return auctionCoreRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 취소하는 서비스 로직
     */

    @Transactional
    public void cancelPurchase(AuctionRefundRequestMessage message) {
        verifyHasBuyerRole(message.buyerInfo());

        //거래 내역 서버 조회(호출 - 업데이트) - 거래 내역 서버
        /**
         *         Receipt receipt = findRefundTargetReceiptForUpdate(message.receiptId());
         *         verifySameBuyer(message.buyerInfo(), receipt.getBuyerId());
         *         receipt.markAsRefund();
         */

        //Auction auction = this.getAuctionForUpdate(receipt.getAuctionId());
        //verifyEndAuction(message.requestTime(), auction.finishedAt());

        //auctionService.cancelPurchase(receipt.getAuctionId(), receipt.getQuantity());

        //포인트 환불 - 회원 서버
        //paymentService.pointTransfer(receipt.getSellerId(), receipt.getBuyerId(), receipt.getPrice() * receipt.getQuantity());

        //거래 내역 서버 업데이트 - 거래 내역 서버
        //receiptRepository.save(receipt);

    }

    /**
     * 경매 목록을 조회하는 서비스 로직(판매자용)
     *
     * @param condition return 판매자용 경매 정보
     */

    public List<BuyerAuctionSimpleInfoResponse> getBuyerAuctionSimpleInfos(AuctionSearchConditionRequest condition) {
        try {
            return auctionCoreRepository.findAllBy(condition).stream()
                    .map(Mapper::convertToBuyerAuctionSimpleInfo)
                    .toList();
        } catch (Exception e) {
            log.error("경매 목록 조회 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 구매한 경매에 대해 상세 조회하는 서비스 로직(구매자용)
     *
     * @param auctionId return 구매자용 경매 정보
     */

    public BuyerAuctionInfoResponse getBuyerAuction(long auctionId) {
        try {
            Auction auction = findAuctionObject(auctionId);
            return Mapper.convertToBuyerAuctionInfo(auction);
        } catch (Exception e) {
            log.error("경매 조회 중 오류 발생", e);
            throw e;
        }
    }

    private void verifyHasBuyerRole(SignInfoRequest buyerInfo) {
        if (!buyerInfo.isType(Role.BUYER)) {
            throw new AuthorizationException("구매자만 환불을 할 수 있습니다.", ErrorCode.P000);
        }
    }

    private void verifyEndAuction(LocalDateTime requestTime, LocalDateTime auctionFinishedAt) {
        if (requestTime.isBefore(auctionFinishedAt)) {
            throw new BadRequestException("종료된 경매만 환불할 수 있습니다.", ErrorCode.P007);
        }
    }

    private void verifySameBuyer(SignInfoRequest buyerInfo, long receiptBuyerId) {
        if (buyerInfo.id() != receiptBuyerId) {
            throw new AuthorizationException("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.", ErrorCode.P004);
        }
    }

    public AuctionInfoRequest getAuctionForUpdate(long auctionId) {
        Auction auction = findAuctionObjectForUpdate(auctionId);

        return Mapper.convertToAuctionInfo(auction);
    }

    private Auction findAuctionObjectForUpdate(long auctionId) {
        return auctionCoreRepository.findByIdForUpdate(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }
}

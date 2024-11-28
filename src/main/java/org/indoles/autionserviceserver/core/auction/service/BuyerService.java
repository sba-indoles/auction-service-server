package org.indoles.autionserviceserver.core.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.enums.ReceiptStatus;
import org.indoles.autionserviceserver.core.auction.dto.Request.*;
import org.indoles.autionserviceserver.core.auction.dto.Response.*;
import org.indoles.autionserviceserver.core.auction.infra.AuctionCoreRepository;
import org.indoles.autionserviceserver.core.auction.utils.MemberFeignClient;
import org.indoles.autionserviceserver.core.auction.utils.ReceiptFeignClient;
import org.indoles.autionserviceserver.global.dto.AuctionPurchaseRequestMessage;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.NotFoundException;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BuyerService {

    private final AuctionCoreRepository auctionCoreRepository;
    private final MemberFeignClient memberFeignClient;
    private final ReceiptFeignClient receiptFeignClient;

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

        auctionCoreRepository.save(auction);

        TransferPointRequest transferRequest = new TransferPointRequest(sellerId, totalAmount);

        TransferPointRequestWrapper requestWrapper = new TransferPointRequestWrapper(buyerInfo, transferRequest);

        TransferPointResponse transferResponse = memberFeignClient.pointTransfer(requestWrapper);

        if (transferResponse == null || transferResponse.remainingPoints() < 0) {
            log.error("포인트 전송 실패: {}", transferRequest);
            throw new RuntimeException("포인트 전송 실패. 판매자 ID: " + sellerId + ", 구매자 ID: " + buyerId);
        }

        CreateReceiptRequest createReceiptRequest = CreateReceiptRequest.builder()
                .receiptId(UUID.randomUUID())
                .productName(auction.getProductName())
                .price(message.price())
                .quantity(message.quantity())
                .receiptStatus(ReceiptStatus.PURCHASED)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .auctionId(message.auctionId())
                .build();

        CreateReceiptRequestWrapper createReceiptRequestWrapper = new CreateReceiptRequestWrapper(buyerInfo, createReceiptRequest);
        receiptFeignClient.createReceipt(createReceiptRequestWrapper);
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
        ReceiptInfoResponse receiptInfoResponse = receiptFeignClient.getReceiptById(message.receiptId());

        AuctionInfoRequest auction = this.getAuctionForUpdate(receiptInfoResponse.auctionId());
        verifyEndAuction(message.requestTime(), auction.finishedAt());

        cacelAuction(receiptInfoResponse.auctionId(), receiptInfoResponse.quantity());

        RefundRequest refundRequest = RefundRequest.builder()
                .receiverId(receiptInfoResponse.sellerId())
                .amount(receiptInfoResponse.price() * receiptInfoResponse.quantity())
                .build();

        RefundPointRequestWrapper requestWrapper = new RefundPointRequestWrapper(message.buyerInfo(), refundRequest);

        RefundResponse refundResponse = memberFeignClient.refundPoint(requestWrapper);
        log.debug("포인트 환불 완료: {}", refundResponse);

        receiptFeignClient.refundReceipt(message.buyerInfo(), message.receiptId());
    }

    public void cacelAuction(long auctionId, long quantity) {
        Auction auction = findAuctionObjectForUpdate(auctionId);
        auction.refundStock(quantity);
        auctionCoreRepository.save(auction);
    }


    /**
     * 경매 목록을 조회하는 서비스 로직
     *
     * @param condition return
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
     * 경매에 대해 상세 조회하는 서비스 로직
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

    private void verifyEndAuction(LocalDateTime requestTime, LocalDateTime auctionFinishedAt) {
        if (requestTime.isBefore(auctionFinishedAt)) {
            throw new BadRequestException("종료된 경매만 환불할 수 있습니다.", ErrorCode.P007);
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

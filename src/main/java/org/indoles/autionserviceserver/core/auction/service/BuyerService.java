package org.indoles.autionserviceserver.core.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.RefundRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.TransferPointRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.*;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.indoles.autionserviceserver.core.auction.infra.AuctionCoreRepository;
import org.indoles.autionserviceserver.core.auction.utils.MemberFeignClient;
import org.indoles.autionserviceserver.global.exception.AuthorizationException;
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
     * @param auctionId   경매번호
     * @param price       구매를 원하는 가격
     * @param quantity    수량
     * @param requestTime 요청 시간
     */

    @Transactional
    public void submitPurchase(long auctionId, long price, long quantity, LocalDateTime requestTime, String authorizationHeader) {
        Auction auction = findAuctionObject(auctionId);
        auction.submit(price, quantity, requestTime);
        auctionCoreRepository.save(auction);

        Long userId = getUserIdFromToken(authorizationHeader);


        TransferPointRequest transferPointRequest = new TransferPointRequest(userId, price);
        TransferPointResponse transferResponse = memberFeignClient.transferPoint(authorizationHeader, transferPointRequest);
        log.info("포인트 전송 응답: {}", transferResponse);
    }

    private Auction findAuctionObject(long auctionId) {
        return auctionCoreRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    private Long getUserIdFromToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // "Bearer "를 제거
        if (jwtTokenProvider.validateToken(token)) {
            SignInInfo signInInfo = jwtTokenProvider.getSignInInfoFromToken(token);
            return signInInfo.id();
        } else {
            throw new AuthorizationException("Unauthorized: JWT validation failed", ErrorCode.AU00);
        }
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 취소하는 서비스 로직
     *
     * @param auctionId 경매번호
     * @param quantity  수량
     */

    @Transactional
    public void cancelPurchase(long auctionId, long quantity, String authorizationHeader) {
        Auction auction = findAuctionObjectForUpdate(auctionId);
        auction.refundStock(quantity);
        auctionCoreRepository.save(auction);

        Long userId = getUserIdFromToken(authorizationHeader);

        RefundRequest refundRequest = new RefundRequest(userId, quantity);
        RefundResponse refundResponse = memberFeignClient.refundPoint(authorizationHeader, refundRequest);
        log.info("포인트 환불 응답: {}", refundResponse);
    }

    private Auction findAuctionObjectForUpdate(long auctionId) {
        return auctionCoreRepository.findByIdForUpdate(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
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
}

package org.indoles.autionserviceserver.core.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.dto.Request.CancelAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.CreateAuctionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.SellerAuctionInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.SellerAuctionSimpleInfoResponse;
import org.indoles.autionserviceserver.core.auction.infra.AuctionCoreRepository;
import org.indoles.autionserviceserver.global.exception.AuthorizationException;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.NotFoundException;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SellerService {

    private final AuctionCoreRepository auctionCoreRepository;

    /**
     * 경매에 상품을 등록하는 서비스 로직
     *
     * @param sellerInfo
     * @param createAuctionRequest
     */

    @Transactional
    public void createAuction(SignInInfo sellerInfo, CreateAuctionRequest createAuctionRequest) {
        try {
            Auction auction = Auction.builder()
                    .sellerId(sellerInfo.id())
                    .productName(createAuctionRequest.productName())
                    .currentPrice(createAuctionRequest.originPrice())
                    .originPrice(createAuctionRequest.originPrice())
                    .currentStock(createAuctionRequest.stock())
                    .originStock(createAuctionRequest.stock())
                    .maximumPurchaseLimitCount(createAuctionRequest.maximumPurchaseLimitCount())
                    .pricePolicy(createAuctionRequest.pricePolicy())
                    .variationDuration(createAuctionRequest.variationDuration())
                    .startedAt(createAuctionRequest.startedAt())
                    .finishedAt(createAuctionRequest.finishedAt())
                    .isShowStock(createAuctionRequest.isShowStock())
                    .build();

            auctionCoreRepository.save(auction);
        } catch (Exception e) {
            log.error("경매 물품 생성 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 경매 시작 전, 등록한 경매에 대해서 취소하는 서비스 로직
     *
     * @param signInInfo 경매를 취소하려는 사용자 정보
     */

    @Transactional
    public void cancelAuction(SignInInfo signInInfo, CancelAuctionRequest command) {
        try {
            if (!signInInfo.isType(Role.SELLER)) {
                throw new AuthorizationException("판매자만 경매를 취소할 수 있습니다.", ErrorCode.A017);
            }

            Auction auction = findAuctionObject(command.auctionId());

            if (!auction.isSeller(signInInfo.id())) {
                throw new AuthorizationException("자신이 등록한 경매만 취소할 수 있습니다.", ErrorCode.A018);
            }
            if (!auction.currentStatus(command.requestTime()).isWaiting()) {
                String message = String.format("시작 전인 경매만 취소할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(),
                        command.requestTime());
                throw new BadRequestException(message, ErrorCode.A019);
            }

            auctionCoreRepository.deleteById(command.auctionId());
        } catch (Exception e) {
            log.error("경매 취소 중 오류 발생", e);
            throw e;
        }
    }

    private Auction findAuctionObject(long auctionId) {
        return auctionCoreRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    /**
     * 경매 목록을 조회하는 서비스 로직(판매자용)
     *
     * @param condition return 판매자용 경매 정보
     */

    public List<SellerAuctionSimpleInfoResponse> getSellerAuctionSimpleInfos(SellerAuctionSearchConditionRequest condition) {
        try {
            return auctionCoreRepository.findAllBy(condition).stream()
                    .map(Mapper::convertToSellerAuctionSimpleInfo)
                    .toList();
        } catch (Exception e) {
            log.error("경매 목록 조회 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 판매한 경매에 대해 상세 조회하는 서비스 로직(판매자용)
     *
     * @param sellerInfo
     * @param auctionId  경매 ID
     *                   return 판매자용 경매 정보
     */

    public SellerAuctionInfoResponse getSellerAuction(SignInInfo sellerInfo, long auctionId) {
        try {
            Auction auction = findAuctionObject(auctionId);

            if (!auction.isSeller(sellerInfo.id())) {
                throw new AuthorizationException("판매자는 자신이 등록한 경매만 조회할 수 있습니다.", ErrorCode.A020);
            }

            return Mapper.convertToSellerAuctionInfo(auction);
        } catch (Exception e) {
            log.error("경매 상세 조회 중 오류 발생", e);
            throw e;
        }
    }
}

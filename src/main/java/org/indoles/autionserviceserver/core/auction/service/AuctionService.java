package org.indoles.autionserviceserver.core.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.controller.SellerAuctionController;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.AuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;
import org.indoles.autionserviceserver.core.auction.repository.AuctionRepository;
import org.indoles.autionserviceserver.core.member.dto.response.SignInInfo;
import org.indoles.autionserviceserver.core.member.entity.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    /**
     * 경매에 상품을 등록하는 서비스 로직
     * @param sellerInfo
     * @param createAuctionCommand
     */

    @Transactional
    public void createAuction(SignInInfo sellerInfo, CreateAuctionCommand createAuctionCommand) {
        try {
            createAuctionCommand.validate();

            Auction auction = Auction.builder()
                    .sellerId(sellerInfo.id())
                    .productName(createAuctionCommand.productName())
                    .currentPrice(createAuctionCommand.originPrice())
                    .originPrice(createAuctionCommand.originPrice())
                    .currentStock(createAuctionCommand.stock())
                    .originStock(createAuctionCommand.stock())
                    .maximumPurchaseLimitCount(createAuctionCommand.maximumPurchaseLimitCount())
                    .pricePolicy(createAuctionCommand.pricePolicy())
                    .variationDuration(createAuctionCommand.variationDuration())
                    .startedAt(createAuctionCommand.startedAt())
                    .finishedAt(createAuctionCommand.finishedAt())
                    .isShowStock(createAuctionCommand.isShowStock())
                    .build();

            AuctionEntity auctionEntity = Auction.toEntity(auction);
            auctionRepository.save(auctionEntity);
        } catch (Exception e) {
            log.error("경매 물품 생성 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 경매 시작 전, 등록한 경매에 대해서 취소하는 서비스 로직
     * @param signInInfo 경매를 취소하려는 사용자 정보
     * @param cancelAuctionCommand 취소할 경매 정보
     */

    @Transactional
    public void cancelAuction(SignInInfo signInInfo,CancelAuctionCommand cancelAuctionCommand){
        if(!signInInfo.isType(Role.SELLER)){
            throw new AuctionException(UNAUTHORIZED_SELLER);
        }

        Auction auction = findAuctionObject(cancelAuctionCommand.auctionId()).toDomain();

        if(!auction.isSeller(signInInfo.id())){
            throw new AuctionException(AUCTION_NOT_FOUND);
        }

        if(!auction.currentStatus(cancelAuctionCommand.requestTime()).isWaiting()) {
            throw new AuctionException(CANNOT_CANCEL_AUCTION);
        }
    }

    private AuctionEntity findAuctionObject(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 진행하는 서비스 로직
     * @param auctionId 경매번호
     * @param price     구매를 원하는 가격
     * @param quantity  수량
     * @param requestTime 요청 시간
     */

    @Transactional
    public void submitPurchase(Long auctionId, Long price, Long quantity, Long requestTime) {
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 취소하는 서비스 로직
     * @param auctionId 경매번호
     * @param quantity  수량
     */

    @Transactional
    public void cancelPurchase(Long auctionId, Long quantity) {
    }

    /**
     * 하나의 경매 물품에 대한 상세 정보를 조회하는 서비스 로직
     * @param auctionId 경매번호
     */

    public AuctionInfo getAuction(Long auctionId) {
    }

    /**
     * 판매한 경매에 대해 조회하는 서비스 로직(판매자용)
     * @param auctionId 경매 ID
     * return 판매자용 경매 정보
     */

    public BuyerAuctionInfo getBuyerAuction(Long auctionId) {

    }

    /**
     * 구매한 경매에 대해 조회하는 서비스 로직(구매자용)
     * @param auctionCondition
     * return 구매자용 경매 정보
     */

    public List<BuyerAuctionSimpleInfo> getBuyerAuctionSimpleInfos(AuctionSearchCondition auctionCondition) {

    }

    /**
     * 경매 목록을 조회하는 서비스 로직(판매자용)
     * @param auctionCondition
     * return 판매자용 경매 정보
     */

    public List<SellerSimpleInfo> getSellerAuctionSimpleInfos(SellerAuctionController auctionCondition) {

    }

    /**
     * 경매 목록을 조회하는 서비스 로직(구매자용)
     * @param auctionCondition
     * return 구매자용 경매 정보
     */

    public List<BuyerAuctionSimpleInfo> getBuyerAuctionSimpleInfos(AuctionSearchCondition auctionCondition) {

    }





//  public List<BuyerAuctionSimpleInfo> getBuyerAuctionSimpleInfos(AuctionSearchCondition condition) {
//    }

//    public BuyerAuctionInfo getBuyerAuction(Long auctionId) {
//    }

//    @Transactional
//    public void cancelAuction(SignInInfo signInInfo, CancelAuctionCommand cancelAuctionCommand) {
//        try {
//            cancelAuctionCommand.validate();
//
//            if (!signInInfo.isType(SELLER)) {
//            throw new AuctionException(AuctionExceptionCode.UNAUTHORIZED_SELLER);
//            }
//
//            Auction auction = findAuctionObject(cancelAuctionCommand.auctionId());
//
//            if (!auction.isSeller(signInInfo.id())) {
//                throw new AuthorizationException("자신이 등록한 경매만 취소할 수 있습니다.", ErrorCode.A018);
//            }
//
//            // 경매 상태 유효성 검사
//            if (!auction.currentStatus(cancelAuctionCommand.requestTime()).isWaiting()) {
//                String message = String.format("시작 전인 경매만 취소할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(),
//                        cancelAuctionCommand.requestTime());
//                throw new BadRequestException(message, ErrorCode.A019);
//            }
//
//            auctionRepository.deleteById(cancelAuctionCommand.auctionId());
//        } catch (Exception e) {
//            log.error("경매 물품 취소 중 오류 발생", e);
//            throw e;
//        }
//    }
}

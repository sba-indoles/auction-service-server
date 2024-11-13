package org.indoles.autionserviceserver.core.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.core.auction.dto.AuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.dto.*;
import org.indoles.autionserviceserver.core.auction.infra.AuctionRepository;
import org.indoles.autionserviceserver.global.exception.AuthorizationException;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.NotFoundException;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    /**
     * 경매에 상품을 등록하는 서비스 로직
     *
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

            auctionRepository.save(auction);
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
    public void cancelAuction(SignInInfo signInInfo, CancelAuctionCommand command) {
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

        auctionRepository.deleteById(command.auctionId());
    }

    private Auction findAuctionObject(long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 진행하는 서비스 로직
     *
     * @param auctionId   경매번호
     * @param price       구매를 원하는 가격
     * @param quantity    수량
     * @param requestTime 요청 시간
     */

    @Transactional
    public void submitPurchase(long auctionId, long price, long quantity, LocalDateTime requestTime) {
        Auction auction = findAuctionObject(auctionId);
        auction.submit(price, quantity, requestTime);
        auctionRepository.save(auction);
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 취소하는 서비스 로직
     *
     * @param auctionId 경매번호
     * @param quantity  수량
     */

    @Transactional
    public void cancelPurchase(long auctionId, long quantity) {
        Auction auction = findAuctionObjectForUpdate(auctionId);
        auction.refundStock(quantity);
        auctionRepository.save(auction);
    }

    private Auction findAuctionObjectForUpdate(long auctionId) {
        return auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A010));
    }

    /**
     * 하나의 경매 물품에 대한 상세 정보를 조회하는 서비스 로직
     *
     * @param auctionId 경매번호
     */

    public AuctionInfo getAuction(long auctionId) {
        Auction auction = findAuctionObject(auctionId);

        return Mapper.convertToAuctionInfo(auction);
    }

    /**
     * 판매한 경매에 대해 조회하는 서비스 로직(판매자용)
     *
     * @param sellerInfo
     * @param auctionId  경매 ID
     *                   return 판매자용 경매 정보
     */

    public SellerAuctionInfo getSellerAuction(SignInInfo sellerInfo, long auctionId) {
        Auction auction = findAuctionObject(auctionId);

        if (!auction.isSeller(sellerInfo.id())) {
            throw new AuthorizationException("판매자는 자신이 등록한 경매만 조회할 수 있습니다.", ErrorCode.A020);
        }

        return Mapper.convertToSellerAuctionInfo(auction);
    }

    /**
     * 구매한 경매에 대해 상세 조회하는 서비스 로직(구매자용)
     *
     * @param auctionId return 구매자용 경매 정보
     */

    public BuyerAuctionInfo getBuyerAuction(long auctionId) {
        Auction auction = findAuctionObject(auctionId);

        return Mapper.convertToBuyerAuctionInfo(auction);
    }

    /**
     * 경매 목록을 조회하는 서비스 로직(판매자용)
     *
     * @param condition return 판매자용 경매 정보
     */

    public List<SellerAuctionSimpleInfo> getSellerAuctionSimpleInfos(SellerAuctionSearchCondition condition) {
        return auctionRepository.findAllBy(condition).stream()
                .map(Mapper::convertToSellerAuctionSimpleInfo)
                .toList();
    }

    /**
     * 경매 목록을 조회하는 서비스 로직(구매자용)
     */

    public List<BuyerAuctionSimpleInfo> getBuyerAuctionSimpleInfos(AuctionSearchCondition condition) {
        return auctionRepository.findAllBy(condition).stream()
                .map(Mapper::convertToBuyerAuctionSimpleInfo)
                .toList();
    }
}

package org.indoles.autionserviceserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.AuctionInfo;
import org.indoles.autionserviceserver.core.auction.dto.BuyerAuctionInfo;
import org.indoles.autionserviceserver.core.auction.dto.SellerAuctionInfo;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {
    public static AuctionInfo convertToAuctionInfo(Auction auction) {
        return AuctionInfo.builder()
                .auctionId(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .stock(auction.getCurrentStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .isShowStock(auction.isShowStock())
                .build();
    }

    public static BuyerAuctionInfo convertToBuyerAuctionInfo(Auction auction) {
        Long currentStock = auction.isShowStock() ? auction.getCurrentStock() : null;
        Long originStock = auction.isShowStock() ? auction.getOriginStock() : null;

        return BuyerAuctionInfo.builder()
                .auctionId(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .originStock(originStock)
                .currentStock(currentStock)
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .build();
    }

    public static SellerAuctionInfo convertToSellerAuctionInfo(Auction auction) {
        return SellerAuctionInfo.builder()
                .auctionId(auction.getId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .originStock(auction.getOriginStock())
                .currentStock(auction.getCurrentStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .isShowStock(auction.isShowStock())
                .build();
    }

    public static Auction convertToAuction(AuctionEntity auctionEntity) {
        return new Auction(
                auctionEntity.getId(),
                auctionEntity.getSellerId(),
                auctionEntity.getProductName(),
                auctionEntity.getOriginPrice(),
                auctionEntity.getCurrentPrice(),
                auctionEntity.getOriginStock(),
                auctionEntity.getCurrentStock(),
                auctionEntity.getMaximumPurchaseLimitCount(),
                auctionEntity.getPricePolicy(),
                auctionEntity.getVariationDuration(),
                auctionEntity.getStartedAt(),
                auctionEntity.getFinishedAt(),
                auctionEntity.isShowStock()
        );
    }

    public static AuctionEntity convertToAuctionEntity(Auction auction) {
        return AuctionEntity.builder()
                .id(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .originStock(auction.getOriginStock())
                .currentStock(auction.getCurrentStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .isShowStock(auction.isShowStock())
                .build();
    }
}

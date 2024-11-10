package org.indoles.autionserviceserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.AuctionInfo;
import org.indoles.autionserviceserver.core.auction.dto.BuyerAuctionInfo;
import org.indoles.autionserviceserver.core.auction.dto.SellerAuctionInfo;

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
                .isShowStock(auction.getIsShowStock())
                .build();
    }

    public static BuyerAuctionInfo convertToBuyerAuctionInfo(Auction auction) {
        Long currentStock = auction.getIsShowStock() ? auction.getCurrentStock() : null;
        Long originStock = auction.getIsShowStock() ? auction.getOriginStock() : null;

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
                .isShowStock(auction.getIsShowStock())
                .build();
    }

}

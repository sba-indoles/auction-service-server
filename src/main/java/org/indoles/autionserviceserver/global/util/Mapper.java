package org.indoles.autionserviceserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.AuctionInfo;
import org.indoles.autionserviceserver.core.auction.dto.BuyerAuctionInfo;

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

}

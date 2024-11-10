package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;
import org.indoles.autionserviceserver.global.entity.BaseEntity;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.AUCTION_NOT_RUNNING;

@Getter
@NoArgsConstructor
public class Auction {

    private Long id;
    private Long sellerId;
    private String productName;
    private Long originPrice;
    private Long currentPrice;
    private Long originStock;
    private Long currentStock;
    private Long maximumPurchaseLimitCount;
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private Boolean isShowStock;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Builder
    public Auction(
            Long id,
            Long sellerId,
            String productName,
            Long originPrice,
            Long currentPrice,
            Long originStock,
            Long currentStock,
            Long maximumPurchaseLimitCount,
            PricePolicy pricePolicy,
            Duration variationDuration,
            Boolean isShowStock,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        this.id = id;
        this.sellerId = sellerId;
        this.productName = productName;
        this.originPrice = originPrice;
        this.currentPrice = currentPrice;
        this.originStock = originStock;
        this.currentStock = currentStock;
        this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
        this.pricePolicy = pricePolicy;
        this.variationDuration = variationDuration;
        this.isShowStock = isShowStock;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;

        ValidateAuction.validateAuctionTime(startedAt, finishedAt);
        ValidateAuction.validateVariationDuration(variationDuration, Duration.between(startedAt, finishedAt));
        ValidateAuction.validateMinimumPrice(startedAt, finishedAt, variationDuration, originPrice, pricePolicy);
    }

    /**
     * 현재 경매 진행 상태를 반환
     */

    public AuctionStatus currentStatus(LocalDateTime requestTime) {
        if (requestTime.isBefore(startedAt)) {
            return AuctionStatus.WAITING;
        }
        if (requestTime.isBefore(finishedAt)) {
            return AuctionStatus.RUNNING;
        }
        return AuctionStatus.FINISHED;
    }

    /**
     * 환불 요청 시 재고 상황
     */

    public void refundStock(long refundStockAmount) {
        long newCurrentStock = this.currentStock + refundStockAmount;

        ValidateAuction.validateStock(this.currentStock, refundStockAmount, this.originStock);
        this.currentStock = newCurrentStock;
    }

    /**
     * 경매 입찰(구매 요청)
     */

    public void submit(long price, long quantity, LocalDateTime requestTime) {
        AuctionStatus currentStatus = ValidateAuction.currentStatus(requestTime, this);

        if (!currentStatus.isRunning()) {
            throw new AuctionException(AUCTION_NOT_RUNNING, currentStatus);
        }

        ValidateAuction.validateAuctionBidStatus(price, quantity, requestTime, this);
        this.currentStock -= quantity;
    }

    /**
     * 구매 제한 체크
     */

    private boolean isOutOfBoundQuantity(long quantity) {
        return quantity > maximumPurchaseLimitCount || quantity <= 0;
    }

    /**
     * 재고 수량 체크
     */

    private boolean hasEnoughStock(long quantity) {
        return currentStock >= quantity;
    }

    /**
     * 해당 경매의 판매자인지 체크
     */

    public boolean isSeller(Long sellerId) {
        return this.sellerId.equals(sellerId);
    }
}

package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.SuccessfulOperationException;

import java.time.Duration;
import java.time.LocalDateTime;


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
            ValidateAuction.validateAuctionBidStatus(requestTime, this);
        }

        verifyCurrentPrice(price, requestTime);
        verifyPurchaseQuantity(quantity);

        this.currentStock -= quantity;
    }

    private void verifyCurrentPrice(long inputPrice, LocalDateTime requestTime) {
        Duration elapsedDuration = Duration.between(startedAt, requestTime);
        long currentVariationCount = elapsedDuration.dividedBy(variationDuration);

        long actualPrice = pricePolicy.calculatePriceAtVariation(originPrice, currentVariationCount);

        if (actualPrice != inputPrice) {
            String message = String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d", actualPrice, inputPrice);
            throw new BadRequestException(message, ErrorCode.A022);
        }
    }

    private void verifyPurchaseQuantity(long quantity) {
        if (isOutOfBoundQuantity(quantity)) {
            String message = String.format("구매 가능 갯수를 초과하거나 0이하의 갯수만큼 구매할 수 없습니다. 요청: %d, 인당구매제한: %d", quantity,
                    maximumPurchaseLimitCount);
            throw new BadRequestException(message, ErrorCode.A030);
        }
        if (!hasEnoughStock(quantity)) {
            String message = String.format("재고가 부족합니다. 현재 재고: %d, 요청 구매 수량: %d", currentStock, quantity);
            throw new SuccessfulOperationException(message, ErrorCode.A012);
        }
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

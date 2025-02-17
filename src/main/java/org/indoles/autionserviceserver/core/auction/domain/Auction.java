package org.indoles.autionserviceserver.core.auction.domain;

import lombok.Builder;
import lombok.Getter;

import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;
import org.indoles.autionserviceserver.global.exception.SuccessfulOperationException;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction.*;

@Slf4j
@Getter
public class Auction {

    private static final int MINIMUM_STOCK_COUNT = 1;
    private static final long NANOS_IN_MINUTE = 60_000_000_000L; // 1분의 나노초
    private static final long MAX_AUCTION_DURATION_NANOS = 60 * NANOS_IN_MINUTE; // 60분의 나노초

    private Long id;
    private final Long sellerId;
    private final String productName;
    private long originPrice;
    private long currentPrice;
    private long originStock;
    private long currentStock;
    private long maximumPurchaseLimitCount;
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private boolean isShowStock;

    @Builder
    public Auction(
            Long id,
            Long sellerId,
            String productName,
            long originPrice,
            long currentPrice,
            long originStock,
            long currentStock,
            long maximumPurchaseLimitCount,
            PricePolicy pricePolicy,
            Duration variationDuration,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            boolean isShowStock
    ) {
        validateAuctionTime(startedAt, finishedAt);
        validateVariationDuration(variationDuration, Duration.between(startedAt, finishedAt));
        validateMinimumPrice(startedAt, finishedAt, variationDuration, originPrice, pricePolicy);

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
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.isShowStock = isShowStock;
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
     *
     * @param refundStockAmount 환불 요청 수량
     */

    public void refundStock(long refundStockAmount) {
        long newCurrentStock = this.currentStock + refundStockAmount;

        validateStock(this.currentStock, refundStockAmount, this.originStock);
        this.currentStock = newCurrentStock;
    }

    /**
     * 경매 입찰(구매 요청)
     *
     * @param price       구매 요청 가격
     * @param quantity    구매 요청 수량
     * @param requestTime 구매 요청 시간
     */

    public void submit(long price, long quantity, LocalDateTime requestTime) {
        AuctionStatus currentStatus = ValidateAuction.currentStatus(requestTime, this);

        if (!currentStatus.isRunning()) {
            validateAuctionBidStatus(requestTime, this);
        }

        verifyCurrentPrice(price, requestTime);
        verifyPurchaseQuantity(quantity);

        this.currentStock -= quantity;
        this.currentPrice = price;
        log.debug("Updated current price to: {}", this.currentPrice);
    }

    private void verifyCurrentPrice(long inputPrice, LocalDateTime requestTime) {
        Duration elapsedDuration = Duration.between(startedAt, requestTime);
        long currentVariationCount = elapsedDuration.dividedBy(variationDuration);
        long actualPrice = pricePolicy.calculatePriceAtVariation(originPrice, currentVariationCount);

        validateBuyPrice(actualPrice, inputPrice);
        this.currentPrice = inputPrice;
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

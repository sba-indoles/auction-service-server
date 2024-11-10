package org.indoles.autionserviceserver.core.auction.domain.validate;

import org.apache.coyote.BadRequestException;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.*;

public class ValidateAuction {

    private static final int MINUMUM_STOCK_COUNT = 1;
    private static final long NANOS_IN_MINUTE = 60_000_000_000L; // 1분의 나노초
    private static final long MAX_AUCTION_DURATION_NANOS = 60 * NANOS_IN_MINUTE; // 60분의 나노초

    /**
     * 경매 시간 유효성 검사
     *
     * @param startedAt
     * @param finishedAt
     * @throws AuctionException
     */

    public static void validateAuctionTime(LocalDateTime startedAt, LocalDateTime finishedAt) {
        Duration duration = Duration.between(startedAt, finishedAt);
        long durationNanos = duration.toNanos();

        if (durationNanos > MAX_AUCTION_DURATION_NANOS) {
            long leftNanoSeconds = durationNanos % NANOS_IN_MINUTE;
            throw new AuctionException(AUCTION_DURATION_OVER_MAX, leftNanoSeconds);
        }

        if (durationNanos % NANOS_IN_MINUTE != 0) {
            long leftNanoSeconds = durationNanos % NANOS_IN_MINUTE;
            throw new AuctionException(AUCTION_DURATION_NOT_MINUTE_UNIT, leftNanoSeconds);
        }
    }

    /**
     * 가격 할인 시간 정책 유효성 검사
     *
     * @param variationDuration
     * @param auctionDuration
     * @throws AuctionException
     */

    public static void validateVariationDuration(Duration variationDuration, Duration auctionDuration) {

        if (!isAllowedDuration(variationDuration, auctionDuration)) {
            throw new AuctionException(AUCTION_VARIATION_DURATION_INVALID, variationDuration);
        }
    }

    private static boolean isAllowedDuration(Duration duration, Duration auctionDuration) {
        if (duration.isZero() || auctionDuration.isZero()) {
            return false;
        }
        long durationSeconds = duration.getSeconds();
        long auctionDurationSeconds = auctionDuration.getSeconds();

        return auctionDurationSeconds % durationSeconds == 0;
    }

    /**
     * 최소 가격 유효성 검사
     *
     * @param startedAt
     * @param finishedAt
     * @param variationDuration
     * @param originPrice
     * @param pricePolicy
     * @throws AuctionException
     */

    public static void validateMinimumPrice(LocalDateTime startedAt, LocalDateTime finishedAt, Duration variationDuration, long originPrice, PricePolicy pricePolicy) {
        Duration totalDuration = Duration.between(startedAt, finishedAt);
        long variationCount = totalDuration.dividedBy(variationDuration) - 1;
        long discountedPrice = pricePolicy.calculatePriceAtVariation(originPrice, variationCount);

        if (discountedPrice <= 0) {
            throw new AuctionException(AUCTION_MINIMUM_PRICE, originPrice, variationCount, discountedPrice);
        }
    }

    /**
     * 재고 유효성 검사
     * @param currentStock
     * @param refundStockAmount
     * @Param originStock
     * @throws AuctionException
     */

    public static void validateStock(long currentStock, long refundStockAmount, long originStock) {
        long newCurrentStock = currentStock + refundStockAmount;

        if (refundStockAmount < MINUMUM_STOCK_COUNT) {
            throw new AuctionException(AUCTION_MINIMUM_REFUND_STOCK_REQUIRED, MINUMUM_STOCK_COUNT, refundStockAmount);
        }

        if (newCurrentStock > originStock) {
            throw new AuctionException(AUCTION_REFUND_EXCEEDS_ORIGINAL_STOCK, originStock, newCurrentStock);
        }
    }

    /**
     * 경매 상태 유효성 검사
     * @param price
     * @param quantity
     * @param requestTime
     */

    public static void validateAuctionBidStatus(long price, long quantity, LocalDateTime requestTime, Auction auction) {
        AuctionStatus currentStatus = auction.currentStatus(requestTime);

        if (!currentStatus.isRunning()) {
            throw new AuctionException(AUCTION_NOT_RUNNING, currentStatus);
        }
        verifyCurrentPrice(price, requestTime, auction);
        verifyPurchaseQuantity(quantity, auction);
    }

    /**
     * 현재 경매 상태 반환
     * @param requestTime
     * @return AuctionStatus
     */

    public static AuctionStatus currentStatus(LocalDateTime requestTime, Auction auction) {
        if (requestTime.isBefore(auction.getStartedAt())) {
            return AuctionStatus.WAITING;
        }

        if (requestTime.isBefore(auction.getFinishedAt())) {
            return AuctionStatus.RUNNING;
        }

        return AuctionStatus.FINISHED;
    }

    private static void verifyCurrentPrice(long inputPrice, LocalDateTime requestTime, Auction auction) {
        Duration elapsedDuration = Duration.between(auction.getStartedAt(), requestTime);

        long currentVariationCount = elapsedDuration.dividedBy(auction.getVariationDuration());
        long actualPrice = auction.getPricePolicy().calculatePriceAtVariation(auction.getOriginPrice(), currentVariationCount);

        if (actualPrice != inputPrice) {
            throw new AuctionException(AUCTION_PRICE_MISMATCH, actualPrice, inputPrice);
        }
    }

    private static void verifyPurchaseQuantity(long quantity, Auction auction) {

        if (quantity > auction.getMaximumPurchaseLimitCount() || quantity <= 0) {
            throw new AuctionException(INVALID_PURCHASE_QUANTITY, quantity, auction.getMaximumPurchaseLimitCount());
        }
        if (auction.getCurrentStock() < quantity) {
            throw new AuctionException(STOCK_NOT_ENOUGH, auction.getCurrentStock(), quantity);
        }
    }
}

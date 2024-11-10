package org.indoles.autionserviceserver.core.auction.domain.validate;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode.*;

public class ValidateAuction {

    private static final int MINIMUM_STOCK_COUNT = 1;
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
     *
     * @param currentStock
     * @param refundStockAmount
     * @throws AuctionException
     * @Param originStock
     */

    public static void validateStock(long currentStock, long refundStockAmount, long originStock) {
        long newCurrentStock = currentStock + refundStockAmount;

        if (refundStockAmount < MINIMUM_STOCK_COUNT) {
            throw new AuctionException(AUCTION_MINIMUM_REFUND_STOCK_REQUIRED, MINIMUM_STOCK_COUNT, refundStockAmount);
        }

        if (newCurrentStock > originStock) {
            throw new AuctionException(AUCTION_REFUND_EXCEEDS_ORIGINAL_STOCK, originStock, newCurrentStock);
        }
    }

    /**
     * 경매 상태 유효성 검사
     *
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
     *
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

    public static void validateProductName(String productName) {
        if (productName.trim().isEmpty()) {
            throw new AuctionException(INVALID_INPUT);
        }
    }

    public static void validateOriginPrice(long originPrice) {
        if (originPrice <= 0) {
            throw new AuctionException(INVALID_INPUT, originPrice);
        }
    }

    public static void validateMaximumPurchaseLimitCount(long maximumPurchaseLimitCount) {
        if (maximumPurchaseLimitCount <= 0) {
            throw new AuctionException(INVALID_INPUT, maximumPurchaseLimitCount);
        }
    }

    public static void validateVariationDuration(Duration variationDuration) {
        if (variationDuration.isNegative() || variationDuration.isZero()) {
            throw new AuctionException(INVALID_INPUT, variationDuration);
        }
    }

    public static void validateStartedAt(LocalDateTime nowAt, LocalDateTime startedAt) {
        if (startedAt.isBefore(nowAt)) {
            throw new AuctionException(INVALID_INPUT, startedAt);
        }
    }

    public static void validateStock(long stock, long maximumPurchaseLimitCount) {
        if (stock < maximumPurchaseLimitCount) {
            throw new AuctionException(INVALID_INPUT, stock, maximumPurchaseLimitCount);
        }
    }

    public static void validateCancelAuction(Auction auction, LocalDateTime requestTime) {
        if (!auction.currentStatus(requestTime).isWaiting()) {
            throw new AuctionException(CANNOT_CANCEL_AUCTION);
        }
    }

    public static void validateStock(long stock) {
        if (stock < 0) {
            throw new AuctionException(STOCK_NOT_ENOUGH, stock);
        }
    }

    public static void validateCurrentPrice(long currentPrice) {
        if (currentPrice <= 0) {
            throw new AuctionException(AUCTION_MINIMUM_PRICE);
        }
    }

    /**
     * 경매 상품에 대해서 조회할 때의 유효성 검사
     *
     * @param from
     * @param to
     * @param size
     */

    public static void validateSizeBetween(int from, int to, int size) {
        if (size < from || size > to) {
            throw new AuctionException(AuctionExceptionCode.INVALID_INPUT,
                    "size는 " + from + " 이상 " + to + " 이하의 값이어야 합니다. 현재 요청: " + size);
        }
    }

    public static void validateOriginStock(long originStock) {
        if (originStock <= 0) {
            throw new AuctionException(AuctionExceptionCode.INVALID_INPUT, originStock);
        }
    }

    public static void validateCurrentStock(long currentStock) {
        if (currentStock < 0) {
            throw new AuctionException(AuctionExceptionCode.INVALID_INPUT, currentStock);
        }
    }

    /**
     * 경매 DTO에서 공통적으로 사용되는 유효성 검사
     *
     * @param value
     * @param fieldName
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new AuctionException(AuctionExceptionCode.INVALID_INPUT, fieldName + "는 Null일 수 없습니다.");
        }
    }
}

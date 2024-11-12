package org.indoles.autionserviceserver.core.auction.domain.validate;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.domain.enums.AuctionStatus;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;

import java.time.Duration;
import java.time.LocalDateTime;


public class ValidateAuction {

    private static final int MINIMUM_STOCK_COUNT = 1;
    private static final long NANOS_IN_MINUTE = 60_000_000_000L; // 1분의 나노초
    private static final long MAX_AUCTION_DURATION_NANOS = 60 * NANOS_IN_MINUTE; // 60분의 나노초

    /**
     * 경매 시간 유효성 검사
     *
     * @param startedAt
     * @param finishedAt
     */

    public static void validateAuctionTime(LocalDateTime startedAt, LocalDateTime finishedAt) {
        Duration duration = Duration.between(startedAt, finishedAt);
        long durationNanos = duration.toNanos();

        if (durationNanos > MAX_AUCTION_DURATION_NANOS) {
            long leftNanoSeconds = durationNanos % NANOS_IN_MINUTE;
            String message = String.format("경매 지속 시간은 최대 60분까지만 가능합니다. 현재 초과되는 나노초: %d초", leftNanoSeconds);
            throw new BadRequestException(message, ErrorCode.A007);
        }

        if (durationNanos % NANOS_IN_MINUTE != 0) {
            long leftNanoSeconds = durationNanos % NANOS_IN_MINUTE;
            String message = String.format("경매 지속 시간은 정확히 분 단위여야 합니다. 현재 남는 나노초: %d초", leftNanoSeconds);
            throw new BadRequestException(message, ErrorCode.A029);
        }
    }

    /**
     * 가격 할인 시간 정책 유효성 검사
     *
     * @param variationDuration
     * @param auctionDuration
     */

    public static void validateVariationDuration(Duration variationDuration, Duration auctionDuration) {

        if (!isAllowedDuration(variationDuration, auctionDuration)) {
            String message = String.format("경매 할인 주기는 경매 지속 시간에서 나누었을때 나누어 떨어져야 합니다. 할인 주기 시간(초): %d, 경매 주기 시간(초): %d",
                    variationDuration.getSeconds(), auctionDuration.getSeconds());
            throw new BadRequestException(message, ErrorCode.A028);
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
     */

    public static void validateMinimumPrice(LocalDateTime startedAt, LocalDateTime finishedAt, Duration variationDuration, long originPrice, PricePolicy pricePolicy) {
        Duration totalDuration = Duration.between(startedAt, finishedAt);
        long variationCount = totalDuration.dividedBy(variationDuration) - 1;
        long discountedPrice = pricePolicy.calculatePriceAtVariation(originPrice, variationCount);

        if (discountedPrice <= 0) {
            String message = String.format("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                    originPrice, variationCount, discountedPrice);
            throw new BadRequestException(message, ErrorCode.A021);
        }
    }

    /**
     * 재고 유효성 검사
     *
     * @param currentStock
     * @param refundStockAmount
     * @Param originStock
     */

    public static void validateStock(long currentStock, long refundStockAmount, long originStock) {
        long newCurrentStock = currentStock + refundStockAmount;

        if (refundStockAmount < MINIMUM_STOCK_COUNT) {
            throw new BadRequestException(
                    String.format("환불할 재고는 %d보다 작을 수 없습니다. inputStock=%s", MINIMUM_STOCK_COUNT, refundStockAmount),
                    ErrorCode.A015);
        }

        if (newCurrentStock > originStock) {
            throw new BadRequestException("환불 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=" + refundStockAmount,
                    ErrorCode.A016);
        }
    }

    /**
     * 경매 상태 유효성 검사
     *
     * @param requestTime
     */

    public static void validateAuctionBidStatus(LocalDateTime requestTime, Auction auction) {
        AuctionStatus currentStatus = auction.currentStatus(requestTime);

        if (!currentStatus.isRunning()) {
            String message = String.format("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: %s", currentStatus);
            throw new BadRequestException(message, ErrorCode.A013);
        }
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
}

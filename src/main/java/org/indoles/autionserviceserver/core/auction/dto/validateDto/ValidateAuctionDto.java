package org.indoles.autionserviceserver.core.auction.dto.validateDto;

import org.indoles.autionserviceserver.core.auction.domain.enums.ReceiptStatus;
import org.indoles.autionserviceserver.global.exception.BadRequestException;
import org.indoles.autionserviceserver.global.exception.ErrorCode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class ValidateAuctionDto {

    public static final String ERROR_PRODUCT_NAME = "상품 이름은 비어있을 수 없습니다.";
    public static final String ERROR_ORIGIN_PRICE = "상품 원가는 0보다 커야 합니다. 상품 원가: %d";
    public static final String ERROR_CURRENT_PRICE = "현재 가격은 0보다 커야 합니다. 현재 가격: %d";
    public static final String ERROR_STOCK = "재고는 0보다 작을 수 없습니다. 재고: %d";
    public static final String ERROR_MAXIMUM_PURCHASE_LIMIT_COUNT = "최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: %d";
    public static final String ERROR_VARIATION_DURATION = "변동 시간 단위는 0보다 커야 합니다. 변동 시간: %s";
    public static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";
    public static final String ERROR_ORIGIN_STOCK = "원래 재고는 0 이하일 수 없습니다. 재고: %d";
    public static final String ERROR_CURRENT_STOCK = "현재 재고는 0보다 작을 수 없습니다. 재고: %d";
    private static final String ERROR_STARTED_AT = "경매의 시작시간은 반드시 요청 시간보다 늦어야 합니다. 요청 시간: %s, 시작 시간: %s";
    private static final String ERROR_AUCTION_TIME = "경매의 시작 시간은 종료 시간보다 이전이어야 합니다. 시작 시간: %s, 종료 시간: %s";


    /**
     * AuctionInfo,BuyerActionInfo DTO에서 사용되는 유효성 검사
     */

    public static void validateProductName(String productName) {
        if (productName.trim().isEmpty()) {
            throw new BadRequestException(ERROR_PRODUCT_NAME, ErrorCode.A001);
        }
    }

    public static void validateOriginPrice(long originPrice) {
        if (originPrice <= 0) {
            throw new BadRequestException(String.format(ERROR_ORIGIN_PRICE, originPrice), ErrorCode.A002);
        }
    }

    public static void validateMaximumPurchaseLimitCount(long maximumPurchaseLimitCount) {
        if (maximumPurchaseLimitCount <= 0) {
            throw new BadRequestException(String.format(ERROR_MAXIMUM_PURCHASE_LIMIT_COUNT, maximumPurchaseLimitCount),
                    ErrorCode.A003);
        }
    }

    public static void validateVariationDuration(Duration variationDuration) {
        if (variationDuration.isNegative() || variationDuration.isZero()) {
            throw new BadRequestException(String.format(ERROR_VARIATION_DURATION, variationDuration), ErrorCode.A005);
        }
    }

    public static void validateStartedAt(LocalDateTime nowAt, LocalDateTime startedAt) {
        if (startedAt.isBefore(nowAt)) {
            String message = String.format(ERROR_STARTED_AT, nowAt, startedAt);
            throw new BadRequestException(message, ErrorCode.A014);
        }
    }

    public static void validateStock(long stock, long maximumPurchaseLimitCount) {
        if (stock < maximumPurchaseLimitCount) {
            throw new BadRequestException(String.format(ERROR_STOCK, stock, maximumPurchaseLimitCount), ErrorCode.A000);
        }
    }

    public static void validateAuctionTime(LocalDateTime startedAt, LocalDateTime finishedAt) {
        if (!startedAt.isBefore(finishedAt)) {
            throw new BadRequestException(String.format(ERROR_AUCTION_TIME, startedAt, finishedAt), ErrorCode.A006);
        }
    }

    public static void validateStock(long stock) {
        if (stock < 0) {
            throw new BadRequestException(String.format(ERROR_STOCK, stock), ErrorCode.A000);
        }
    }

    public static void validateCurrentPrice(long currentPrice) {
        if (currentPrice <= 0) {
            throw new BadRequestException(String.format(ERROR_CURRENT_PRICE, currentPrice), ErrorCode.A011);
        }
    }

    /**
     * AuctionSearch DTO에서 사용되는 유효성 검사
     *
     * @param from
     * @param to
     * @param size
     */

    public static void validateSizeBetween(int from, int to, int size) {
        if (size < from || size > to) {
            throw new BadRequestException("size는 " + from + " 이상 " + to + " 이하의 값이어야 합니다.", ErrorCode.G001);
        }
    }

    public static void validateOriginStock(long originStock) {
        if (originStock <= 0) {
            throw new BadRequestException(String.format(ERROR_ORIGIN_STOCK, originStock), ErrorCode.A000);
        }
    }

    public static void validateCurrentStock(long currentStock) {
        if (currentStock < 0) {
            throw new BadRequestException(String.format(ERROR_CURRENT_STOCK, currentStock), ErrorCode.A000);
        }
    }

    /**
     * 입찰 DTO에서 사용되는 유효성 검사
     *
     * @param price
     */

    public static void validatePrice(long price) {
        if (price < 0) {
            throw new BadRequestException("경매 입찰 요청 가격은 음수일 수 없습니다. 요청가격: " + price, ErrorCode.A026);
        }
    }

    public static void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("경매 입찰 요청 수량은 0보다 커야합니다. 요청수량: " + quantity, ErrorCode.A027);
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
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }

    /**
     * 경매 조회 DTO에서 사용되는 유효성 검사
     */
    public static void validateOffset(int offset) {
        if (offset < 0) {
            throw new BadRequestException("offset은 0 이상의 값이어야 합니다.", ErrorCode.G002);
        }
    }

    /**
     * 경매 입찰 시 사용되는 DTO의 유효성 검사
     */

    public static void validateReceiverId(Long receiverId) {
        if (receiverId == null || receiverId <= 0) {
            throw new BadRequestException("판매자의 ID를 찾을 수 없습니다.", ErrorCode.P010);
        }
    }

    public static void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("포인트 전송 금액은 null이거나 0보다 작을 수 없습니다.", ErrorCode.P009);
        }
    }

    /**
     * 경매 입찰 시 거래 내역 서버에 전송할 DTO의 유효성 검사
     */

    public static void validateAuctionId(long auctionId) {
        if (auctionId <= 0) {
            throw new BadRequestException("경매 ID를 찾을 수 없습니다.", ErrorCode.A032);
        }
    }

    public static void validateBuyerId(long buyerId) {
        if (buyerId <= 0) {
            throw new BadRequestException("구매자 ID를 찾을 수 없습니다.", ErrorCode.A033);
        }
    }

    public static void validateSellerId(long sellerId) {
        if (sellerId <= 0) {
            throw new BadRequestException("판매자 ID를 찾을 수 없습니다.", ErrorCode.A034);
        }
    }

    public static void validateReceiptStatus(ReceiptStatus receiptStatus) {
        validateNotNull(receiptStatus, "거래 내역 상태");
    }

    public static void validateReceiptId(UUID receiptId) {
        validateNotNull(receiptId, "거래 내역 ID");
    }
}

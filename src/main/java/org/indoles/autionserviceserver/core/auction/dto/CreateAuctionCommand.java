package org.indoles.autionserviceserver.core.auction.dto;

import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction.*;
import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction.validateNotNull;

/**
 * 경매 물품 생성을 위한 객체
 *
 * @param productName               상품 이름
 * @param originPrice               상품 원가
 * @param stock                     재고 수량
 * @param maximumPurchaseLimitCount 최대 구매 제한 수량 (인당 구매 가능 수량)
 * @param pricePolicy               경매 유형
 * @param variationDuration         가격 변동 주기
 * @param requestTime               요청 시간
 * @param startedAt                 경매 시작 시간
 * @param finishedAt                경매 종료 시간
 */
public record CreateAuctionCommand(
        String productName,
        Long originPrice,
        Long stock,
        Long maximumPurchaseLimitCount,
        PricePolicy pricePolicy,
        Duration variationDuration,
        LocalDateTime requestTime,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Boolean isShowStock
) {
    public CreateAuctionCommand {
        validateNotNull(productName, "상품 이름");
        validateNotNull(pricePolicy, "경매 유형");
        validateNotNull(variationDuration, "가격 변동 주기");
        validateNotNull(requestTime, "요청 시간");
        validateNotNull(startedAt, "경매 시작 시간");
        validateNotNull(finishedAt, "경매 종료 시간");
    }

    public void validate() {
        validateProductName(productName);
        validateOriginPrice(originPrice);
        validateMaximumPurchaseLimitCount(maximumPurchaseLimitCount);
        validateVariationDuration(variationDuration);
        validateAuctionTime(startedAt, finishedAt);
        validateStartedAt(requestTime, startedAt);
        validateStock(stock, maximumPurchaseLimitCount);
    }
}


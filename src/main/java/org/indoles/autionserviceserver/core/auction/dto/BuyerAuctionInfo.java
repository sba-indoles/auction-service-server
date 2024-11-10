package org.indoles.autionserviceserver.core.auction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.indoles.autionserviceserver.core.auction.domain.validate.ValidateAuction.*;

/**
 * 경매의 구매자가 조회할 수 있는 경매 상품의 정보
 *
 * @param auctionId                 경매 ID,
 * @param sellerId                  판매자 ID,
 * @param productName               상품 이름,
 * @param originPrice               상품 원가,
 * @param currentPrice              현재 가격,
 * @param originStock               상품의 원래 재고,
 * @param currentStock              현재 재고,
 * @param maximumPurchaseLimitCount 최대 구매 가능한 개수,
 * @param pricePolicy               경매 유형,
 * @param variationDuration         가격 변동 주기,
 * @param startedAt                 경매 시작 시간,
 * @param finishedAt                경매 종료 시간
 */

@Builder
public record BuyerAuctionInfo(
        Long auctionId,
        Long sellerId,
        String productName,
        Long originPrice,
        Long currentPrice,
        @JsonInclude(NON_NULL)
        Long originStock,
        @JsonInclude(NON_NULL)
        Long currentStock,
        Long maximumPurchaseLimitCount,
        PricePolicy pricePolicy,
        Duration variationDuration,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
    public BuyerAuctionInfo {
        validateNotNull(auctionId, "경매 ID");
        validateNotNull(sellerId, "판매자 ID");
        validateNotNull(productName, "상품 이름");
        validateNotNull(pricePolicy, "경매 유형");
        validateNotNull(variationDuration, "가격 변동 주기");
        validateNotNull(startedAt, "경매 시작 시간");
        validateNotNull(finishedAt, "경매 종료 시간");

        validateProductName(productName);
        validateOriginPrice(originPrice);
        validateCurrentPrice(currentPrice);
        validateMaximumPurchaseLimitCount(maximumPurchaseLimitCount);
        validateVariationDuration(variationDuration);
    }
}

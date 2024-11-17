package org.indoles.autionserviceserver.core.auction.dto.Response;

import lombok.Builder;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.indoles.autionserviceserver.core.auction.dto.validateDto.ValidateAuctionDto.*;

/**
 * 판매자가 조회할 수 있는 경매 정보
 *
 * @param auctionId                 경매 ID
 * @param productName               상품 이름
 * @param originPrice               판매상품의 원래 가격
 * @param currentPrice              현재 경매에 설정된 가격
 * @param originStock               판매상품의 원래 재고 개수
 * @param currentStock              현재 경매에 남은 재고 개수
 * @param maximumPurchaseLimitCount 최대 구매 가능한 개수
 * @param pricePolicy               경매 유형
 * @param variationDuration         가격 변동 주기
 * @param startedAt                 경매 시작 시간
 * @param finishedAt                경매 종료 시간
 * @param isShowStock               재고를 보여줄지 여부
 */

@Builder
public record SellerAuctionInfoResponse(
        Long auctionId,
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
        Boolean isShowStock
) {
    public SellerAuctionInfoResponse {
        validateNotNull(auctionId, "경매 ID");
        validateNotNull(productName, "상품 이름");
        validateNotNull(pricePolicy, "경매 유형");
        validateNotNull(variationDuration, "가격 변동 주기");
        validateNotNull(startedAt, "경매 시작 시간");
        validateNotNull(finishedAt, "경매 종료 시간");

        validateProductName(productName);
        validateOriginPrice(originPrice);
        validateCurrentPrice(currentPrice);
        validateOriginStock(originStock);
        validateCurrentStock(currentStock);
        validateMaximumPurchaseLimitCount(maximumPurchaseLimitCount);
        validateVariationDuration(variationDuration);
    }
}

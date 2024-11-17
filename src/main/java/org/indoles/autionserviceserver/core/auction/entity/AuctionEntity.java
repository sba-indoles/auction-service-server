package org.indoles.autionserviceserver.core.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.antlr.v4.runtime.misc.NotNull;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.entity.utils.PricePolicyConverter;

@Getter
@Entity
@Table(name = "AUCTION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long sellerId;

    @NotNull
    private String productName;

    @NotNull
    private long originPrice;

    @NotNull
    private long currentPrice;

    @NotNull
    private long originStock;

    private long currentStock;

    @NotNull
    private long maximumPurchaseLimitCount;

    @Convert(converter = PricePolicyConverter.class)
    private PricePolicy pricePolicy;

    @NotNull
    private Duration variationDuration;

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime finishedAt;

    @NonNull
    private boolean isShowStock;

    @Builder
    private AuctionEntity(
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
}

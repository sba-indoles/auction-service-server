package org.indoles.autionserviceserver.core.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.antlr.v4.runtime.misc.NotNull;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
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
    private Long originPrice;

    @NotNull
    private Long currentPrice;

    @NotNull
    private Long originStock;

    @NotNull
    private Long currentStock;

    @NotNull
    private Long maximumPurchaseLimitCount;

    @Convert(converter = PricePolicyConverter.class)
    private PricePolicy pricePolicy;

    @NotNull
    private Duration variationDuration;

    @NonNull
    private boolean isShowStock;

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime finishedAt;

    @Builder
    private AuctionEntity(
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

    public Auction toDomain() {
        return Auction.builder()
                .id(this.id)
                .sellerId(this.sellerId)
                .productName(this.productName)
                .originPrice(this.originPrice)
                .currentPrice(this.currentPrice)
                .originStock(this.originStock)
                .currentStock(this.currentStock)
                .maximumPurchaseLimitCount(this.maximumPurchaseLimitCount)
                .pricePolicy(this.pricePolicy)
                .variationDuration(this.variationDuration)
                .isShowStock(this.isShowStock)
                .startedAt(this.startedAt)
                .finishedAt(this.finishedAt)
                .build();
    }
}

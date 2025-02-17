package org.indoles.autionserviceserver.core.auction.domain.enums;

import lombok.Getter;

@Getter
public enum ReceiptStatus {

    PURCHASED("구매완료"),
    REFUND("환불완료");

    private final String description;

    ReceiptStatus(String description) {
        this.description = description;
    }
}


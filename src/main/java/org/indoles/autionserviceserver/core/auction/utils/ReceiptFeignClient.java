package org.indoles.autionserviceserver.core.auction.utils;

import org.indoles.autionserviceserver.core.auction.dto.Request.CreateReceiptRequestWrapper;
import org.indoles.autionserviceserver.core.auction.dto.Request.SignInfoRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.ReceiptInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "receipt-service", url = "${receipt-service.url}")
public interface ReceiptFeignClient {

    @PostMapping("/receipts/create")
    void createReceipt(
            @RequestBody CreateReceiptRequestWrapper createReceiptRequestWrapper
            );

    @GetMapping("/receipts/find/{receiptId}")
    ReceiptInfoResponse getReceiptById(
            @PathVariable("receiptId") UUID receiptId
    );

    @PutMapping("/receipts/refund/{receiptId}")
    void refundReceipt(
            @RequestBody SignInfoRequest signInfoRequest,
            @PathVariable("receiptId") UUID receiptId
    );
}

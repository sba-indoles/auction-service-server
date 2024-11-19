package org.indoles.autionserviceserver.core.auction.utils;

import org.indoles.autionserviceserver.core.auction.dto.Request.CreateReceiptRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.ReceiptInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.TransactionInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "receipt-service", url = "http://localhost:9090")
public interface ReceiptFeignClient {

    @PostMapping("/receipts/create")
    void createReceipt(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateReceiptRequest request
    );

    @GetMapping("/receipts/find/{receiptId}")
    ReceiptInfoResponse getReceiptById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("receiptId") UUID receiptId
    );
}
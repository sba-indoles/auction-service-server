package org.indoles.autionserviceserver.core.auction.utils;

import org.indoles.autionserviceserver.core.auction.dto.Request.TransactionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.ReceiptInfoResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.TransactionInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "receipt-service", url = "http://localhost:9090")
public interface ReceiptFeignClient {

    @GetMapping("/receipts/transactions/{userId}")
    List<TransactionInfoResponse> getTransactionsByUserId(@PathVariable("userId") Long userId,
                                                          @RequestParam("offset") int offset,
                                                          @RequestParam("size") int size);

    @GetMapping("/receipts/transactions/{receiptId}")
    ReceiptInfoResponse getReceiptById(@PathVariable("receiptId") UUID receiptId);

    @PostMapping("/receipts/transactions")
    void createTransaction(@RequestBody TransactionRequest request);
}

package org.indoles.autionserviceserver.core.auction.utils;

import org.indoles.autionserviceserver.core.auction.dto.Request.*;
import org.indoles.autionserviceserver.core.auction.dto.Response.RefundResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.TransferPointResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member-service", url = "${member-service.url}")
public interface MemberFeignClient {

    @PostMapping("/members/points/transfer")
    TransferPointResponse pointTransfer(
            @RequestBody TransferPointRequestWrapper requestWrapper
    );

    @PostMapping("/members/points/refund")
    RefundResponse refundPoint(
            @RequestBody RefundPointRequestWrapper requestWrapper
    );
}


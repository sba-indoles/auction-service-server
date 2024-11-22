package org.indoles.autionserviceserver.core.auction.utils;

import org.indoles.autionserviceserver.core.auction.dto.Request.RefundRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.TransferPointRequest;
import org.indoles.autionserviceserver.core.auction.dto.Response.RefundResponse;
import org.indoles.autionserviceserver.core.auction.dto.Response.TransferPointResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "member-service", url = "${member-service.url}")
public interface MemberFeignClient {

    @PostMapping("/members/points/transfer")
    TransferPointResponse pointTransfer(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TransferPointRequest transferPointRequest);

    @PostMapping("/members/points/refund")
    RefundResponse refundPoint(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RefundRequest refundRequest
    );
}


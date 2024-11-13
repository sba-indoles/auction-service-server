package org.indoles.autionserviceserver.core.auction.controller.interfaces;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "memberService", url = "http://localhost:7070")
public interface MemberServiceClient {
}
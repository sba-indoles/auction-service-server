package org.indoles.autionserviceserver.core.auction.controller.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "memberService", url = "http://localhost:7070")
public interface MemberServiceClient {

    @GetMapping("/auth/validate")
    SignInInfo validateSession(@RequestHeader("X-Session-Id") String sessionId);
}
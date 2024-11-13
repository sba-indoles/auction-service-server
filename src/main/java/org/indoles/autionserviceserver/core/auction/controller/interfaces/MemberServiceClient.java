package org.indoles.autionserviceserver.core.auction.controller.interfaces;

import org.indoles.autionserviceserver.core.auction.dto.SignInInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "memberService", url = "http://localhost:7070")
public interface MemberServiceClient {

    SignInInfo getSignInInfo();
}
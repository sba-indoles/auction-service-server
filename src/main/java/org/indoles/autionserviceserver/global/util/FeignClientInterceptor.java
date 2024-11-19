package org.indoles.autionserviceserver.global.util;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void apply(RequestTemplate template) {
        String token = getCurrentToken();
        if (token != null) {
            template.header("Authorization", "Bearer " + token);
            log.debug("Adding Authorization header: Bearer {}", token); // 추가된 로그
        } else {
            log.warn("No JWT token found, Authorization header not added."); // 경고 로그
        }
    }

    private String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        return null; // 토큰이 없는 경우 null 반환
    }
}

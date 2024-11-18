package org.indoles.autionserviceserver.global.config;

import org.indoles.autionserviceserver.core.auction.controller.currentTime.CurrentTimeArgumentResolver;
import org.indoles.autionserviceserver.global.util.LoginArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginArgumentResolver loginArgumentResolver;
    private final CurrentTimeArgumentResolver currentTimeArgumentResolver;

    public WebConfig(LoginArgumentResolver loginArgumentResolver, CurrentTimeArgumentResolver currentTimeArgumentResolver) {
        this.loginArgumentResolver = loginArgumentResolver;
        this.currentTimeArgumentResolver = currentTimeArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver);
        resolvers.add(currentTimeArgumentResolver);
    }
}



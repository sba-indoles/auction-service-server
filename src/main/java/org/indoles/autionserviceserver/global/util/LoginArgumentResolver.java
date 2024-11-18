package org.indoles.autionserviceserver.global.util;

import org.indoles.autionserviceserver.core.auction.controller.interfaces.Login;
import org.indoles.autionserviceserver.core.auction.dto.Request.SignInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public LoginArgumentResolver(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Login.class) != null &&
                SignInfoRequest.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public SignInfoRequest resolveArgument(MethodParameter parameter,
                                           ModelAndViewContainer mavContainer,
                                           NativeWebRequest webRequest,
                                           WebDataBinderFactory binderFactory) throws Exception {
        String authorizationHeader = webRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return jwtTokenProvider.getSignInInfoFromToken(token);
        }
        throw new IllegalArgumentException("Authorization header is missing or invalid");
    }
}



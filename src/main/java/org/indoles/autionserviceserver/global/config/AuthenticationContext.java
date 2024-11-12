package org.indoles.autionserviceserver.global.config;

import lombok.Getter;
import org.indoles.autionserviceserver.core.member.dto.response.SignInInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

    private SignInInfo principal;

    public void setPrincipal(SignInInfo principal) {
        this.principal = principal;
    }
}
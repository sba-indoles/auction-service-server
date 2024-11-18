package org.indoles.autionserviceserver.global.config;

import lombok.Getter;
import org.indoles.autionserviceserver.core.auction.dto.Request.SignInfoRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

    private SignInfoRequest principal;

    public void setPrincipal(SignInfoRequest principal) {
        this.principal = principal;
    }
}


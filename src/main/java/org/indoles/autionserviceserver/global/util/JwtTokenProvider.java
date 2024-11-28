package org.indoles.autionserviceserver.global.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.indoles.autionserviceserver.core.auction.domain.enums.Role;
import org.indoles.autionserviceserver.core.auction.dto.Request.SignInfoRequest;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @PostConstruct
    protected void init() {
        log.debug("Encoded secret key: {}", secretKey);
    }

    public SignInfoRequest getSignInInfoFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
            Long userId = Long.valueOf(claims.getSubject());
            String roleStr = claims.get("role", String.class);
            Role role = Role.valueOf(roleStr);
            log.debug("Extracted userId: {}, role: {}", userId, roleStr);
            return new SignInfoRequest(userId, role);
        } catch (Exception e) {
            log.error("Error extracting SignInInfo from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }
}

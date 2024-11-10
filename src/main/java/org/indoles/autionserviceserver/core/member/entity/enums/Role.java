package org.indoles.autionserviceserver.core.member.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.member.entity.exception.MemberException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.indoles.autionserviceserver.core.member.entity.exception.MemberExceptionCode.*;

@Getter
@RequiredArgsConstructor
public enum Role {

    SELLER("판매자"),
    BUYER("구매자");

    private final String description;

    @JsonValue
    private String getDescription() {
        return description;
    }

    @JsonCreator
    public Role from(String description) {
        if (roleMap.containsKey(description)) {
            return roleMap.get(description);
        }
        throw new MemberException(ROLE_NOT_FOUND, description);
    }

    private static final Map<String, Role> roleMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(Role::getDescription, Function.identity())));

    public static Role find(final String userRole) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(userRole))
                .findAny()
                .orElseThrow(() -> new MemberException(ROLE_NOT_FOUND, userRole));
    }
}

package org.indoles.autionserviceserver.core.member.dto.request;

import org.indoles.autionserviceserver.core.member.entity.enums.Role;

public record MemberChargePointRequest(
        Long memberId,
        Role role,
        Long amount
) {
}

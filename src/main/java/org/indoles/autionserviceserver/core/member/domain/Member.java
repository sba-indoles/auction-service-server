package org.indoles.autionserviceserver.core.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.indoles.autionserviceserver.core.member.domain.validate.ValidateMember;
import org.indoles.autionserviceserver.core.member.entity.MemberEntity;
import org.indoles.autionserviceserver.core.member.entity.enums.Role;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    private Long id;
    private String signInId;
    private String password;
    private Role role;
    private Point point;


    @Builder
    public Member(
            Long id,
            String signInId,
            String password,
            Role role,
            Point point
    ) {
        ValidateMember validateMember = new ValidateMember();

        validateMember.validateSignInId(signInId);
        validateMember.validateSignInPassword(password);

        this.id = id;
        this.signInId = signInId;
        this.password = password;
        this.role = role;
        this.point = point;
    }

    /**
     * 다른 사용자에게 포인트 송금
     * @param receiver 포인트를 받을 사용자
     * @param amount 지불할 포인트
     */

    public void pointTransfer(Member receiver, Long amount) {
        point.minus(amount);
        receiver.getPoint().plus(amount);
    }

    /**
     * 포인트 사용(결제)
     **/

    public void usePoint(long price) {
        point.minus(price);
    }

    /**
     * 포인트 충전
     **/
    public void chargePoint(long price) {
        point.plus(price);
    }

    /**
     * 비밀번호 확인
     **/

    public boolean confirmPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * 로그인 아이디 확인
     **/

    public boolean isSameMember(String signInId) {
        return this.signInId.equals(signInId);
    }

    /**
     * 구매자 여부 확인
     */

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }

    /**
     * 초기 사용자 생성
     */

    public static Member createMemberWithRole(
            String signInId,
            String password,
            String userRole
    ) {
        Role role = Role.find(userRole);

        return Member.builder()
                .signInId(signInId)
                .password(password)
                .role(role)
                .point(new Point(0L))
                .build();
    }

    public static MemberEntity toEntity(Member member) {
        return MemberEntity.builder()
                .id(member.getId())
                .signInId(member.getSignInId())
                .password(member.getPassword())
                .role(member.getRole())
                .point(member.getPoint().getValue())
                .build();
    }
}

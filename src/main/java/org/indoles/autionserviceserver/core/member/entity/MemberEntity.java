package org.indoles.autionserviceserver.core.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.indoles.autionserviceserver.core.member.domain.Member;
import org.indoles.autionserviceserver.core.member.domain.Point;
import org.indoles.autionserviceserver.core.member.entity.enums.Role;

@Getter
@Entity
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String signInId;

    @NonNull
    private String password;

    @NonNull
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @NonNull
    private Long point;

    @Builder
    private MemberEntity(
            Long id,
            String signInId,
            String password,
            Role role,
            Long point
    ) {
        this.id = id;
        this.signInId = signInId;
        this.password = password;
        this.role = role;
        this.point = point;
    }

    public Member toDomain() {
        return Member.builder()
                .id(this.id)
                .signInId(this.signInId)
                .password(this.password)
                .role(this.role)
                .point(new Point(this.point))
                .build();
    }
}

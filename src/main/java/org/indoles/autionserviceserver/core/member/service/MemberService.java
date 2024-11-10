package org.indoles.autionserviceserver.core.member.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.autionserviceserver.core.member.domain.Member;
import org.indoles.autionserviceserver.core.member.domain.Point;
import org.indoles.autionserviceserver.core.member.dto.request.SignInRequestInfo;
import org.indoles.autionserviceserver.core.member.dto.request.SignUpRequestInfo;
import org.indoles.autionserviceserver.core.member.dto.response.SignInInfo;
import org.indoles.autionserviceserver.core.member.entity.MemberEntity;
import org.indoles.autionserviceserver.core.member.entity.exception.MemberException;
import org.indoles.autionserviceserver.core.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.indoles.autionserviceserver.core.member.entity.exception.MemberExceptionCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void signUp(SignUpRequestInfo signUpRequestInfo) {
        try {
            if (memberRepository.existsBySignInId(signUpRequestInfo.signUpId())) {
                throw new MemberException(ALREADY_EXISTS);
            }
            Member member = Member.createMemberWithRole(
                    signUpRequestInfo.signUpId(),
                    signUpRequestInfo.password(),
                    signUpRequestInfo.userRole()
            );

            MemberEntity memberEntity = Member.toEntity(member);
            memberRepository.save(memberEntity);
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            throw e;
        }
    }

    public SignInInfo signIn(SignInRequestInfo signInRequestInfo) {
        try {
            MemberEntity memberEntity = memberRepository.findBySignInId(signInRequestInfo.signInId())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

            // MemberEntity -> Member로 변환
            Member member = memberEntity.toDomain();

            if (!member.confirmPassword(signInRequestInfo.password())) {
                throw new MemberException(WRONG_PASSWORD);
            }

            return new SignInInfo(member.getId(), member.getRole());
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            throw e;
        }
    }

    @Transactional
    public void chargePoint(SignInInfo memberInfo, long chargePoint) {
        try {
            Point points = new Point(chargePoint);

            MemberEntity memberEntity = memberRepository.findById(memberInfo.id())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

            Member member = memberEntity.toDomain();
            member.chargePoint(chargePoint);

            memberRepository.save(Member.toEntity(member));
        } catch (Exception e) {
            log.error("포인트 충전 중 오류 발생", e);
            throw e;
        }
    }
}

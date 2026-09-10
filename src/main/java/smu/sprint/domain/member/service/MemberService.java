package smu.sprint.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.member.dto.MemberSignUpRequest;
import smu.sprint.domain.member.dto.MemberSignUpResponse;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.auth.Roles;
import smu.sprint.global.security.jwt.JwtDTO;
import smu.sprint.global.security.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER);
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Roles.ROLE_USER) // 클라이언트 입력과 무관하게 서버가 강제로 일반 회원 권한을 부여
                .build();
        memberRepository.save(member);

        CustomUserDetails customUserDetails = new CustomUserDetails(
                member.getEmail(), member.getPassword(), member.getRole()
        );
        JwtDTO token = new JwtDTO(
                jwtUtil.createJwtAccessToken(customUserDetails),
                jwtUtil.createJwtRefreshToken(customUserDetails)
        );

        return new MemberSignUpResponse(member.getEmail(), token);
    }

}

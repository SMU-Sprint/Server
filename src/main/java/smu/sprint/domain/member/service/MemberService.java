package smu.sprint.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.mail.service.EmailVerificationService;
import smu.sprint.domain.member.dto.*;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.auth.Roles;
import smu.sprint.global.security.jwt.JwtDTO;
import smu.sprint.global.security.jwt.JwtUtil;
import smu.sprint.global.security.jwt.TokenRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    // 사람이 눈으로 옮겨 적기 쉬운 문자만으로 임시 비밀번호를 구성 (0/O, 1/l/I 등 혼동되는 문자 제외)
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIALS = "!@#$%^&*";
    private static final String TEMP_PASSWORD_POOL = LETTERS + DIGITS + SPECIALS;

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailVerificationService emailVerificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER);
        }
        emailVerificationService.verifyCode(request.email(), request.code());

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

        log.info("[ MemberService ]: 회원가입 완료 - email={}", member.getEmail());
        return new MemberSignUpResponse(member.getEmail(), token);
    }

    @Transactional
    public void changePassword(CustomUserDetails customUserDetails, PasswordChangeRequest request) {
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new MemberException(MemberErrorCode.PASSWORD_MISMATCH);
        }

        String email = customUserDetails.getUsername();
        emailVerificationService.verifyCode(email, request.code());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (passwordEncoder.matches(request.newPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.PASSWORD_NOT_CHANGED);
        }

        member.changePassword(passwordEncoder.encode(request.newPassword()));
        // 로그인 상태는 유지하므로 저장된 RefreshToken은 그대로 둔다.
        log.info("[ MemberService ]: 비밀번호 변경 완료 - email={}", email);
    }

    @Transactional
    public void resetPassword(FindPasswordRequest request) {
        emailVerificationService.verifyCode(request.email(), request.code());

        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        String temporaryPassword = generateTemporaryPassword();
        member.changePassword(passwordEncoder.encode(temporaryPassword));
        // 임시 비밀번호 발급 시 탈취된 세션이 남아있지 않도록 기존 RefreshToken을 폐기 (재로그인 필요)
        tokenRepository.findByMember(member).ifPresent(tokenRepository::delete);

        emailVerificationService.sendTemporaryPassword(member.getEmail(), temporaryPassword);
        log.info("[ MemberService ]: 임시 비밀번호 발급 완료 - email={}", member.getEmail());
    }

    private String generateTemporaryPassword() {
        List<Character> chars = new ArrayList<>(List.of(
                pickRandomChar(LETTERS), pickRandomChar(DIGITS), pickRandomChar(SPECIALS)
        ));
        for (int i = chars.size(); i < TEMP_PASSWORD_LENGTH; i++) {
            chars.add(pickRandomChar(TEMP_PASSWORD_POOL));
        }
        Collections.shuffle(chars, secureRandom);
        return chars.stream().map(String::valueOf).collect(Collectors.joining());
    }

    private char pickRandomChar(String source) {
        return source.charAt(secureRandom.nextInt(source.length()));
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMemberInfo(CustomUserDetails customUserDetails, MemberInfoUpdateRequest request) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateInfo(request.name(), request.height(), request.weight(), request.age(), request.gender());

        log.info("[ MemberService ]: 회원 정보 변경 완료 - email={}", member.getEmail());
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public void withdraw(CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        // email을 변형하기 전에 먼저 조회/삭제해야 원래 email로 저장된 Token을 정확히 찾을 수 있다.
        tokenRepository.findByMember(member).ifPresent(tokenRepository::delete);
        member.withdraw();
        log.info("[ MemberService ]: 회원 탈퇴 완료 - email={}", customUserDetails.getUsername());
    }

}

package smu.sprint.domain.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smu.sprint.domain.mail.dto.EmailVerificationRequest;
import smu.sprint.domain.mail.entity.EmailVerification;
import smu.sprint.domain.mail.repository.EmailVerificationRepository;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.global.code.EmailVerificationErrorCode;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.EmailVerificationException;
import smu.sprint.global.exception.MemberException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final Duration CODE_TTL = Duration.ofMinutes(10);
    private static final Duration REISSUE_COOLDOWN = Duration.ofSeconds(60);
    private static final int CODE_LENGTH = 6;

    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void issueCode(EmailVerificationRequest request) {
        String email = request.email();

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER);
        }

        Optional<EmailVerification> existing = emailVerificationRepository.findById(email);
        existing.ifPresent(verification -> {
            if (verification.getIssuedAt().plus(REISSUE_COOLDOWN).isAfter(LocalDateTime.now())) {
                throw new EmailVerificationException(EmailVerificationErrorCode.VERIFICATION_REQUEST_TOO_FREQUENT);
            }
        });

        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();

        // 이메일당 코드를 하나만 유지 (재발급 시 이전 코드는 자동으로 무효화됨)
        EmailVerification verification = existing
                .map(v -> {
                    v.reissue(code, now);
                    return v;
                })
                .orElseGet(() -> EmailVerification.builder()
                        .email(email)
                        .code(code)
                        .issuedAt(now)
                        .build());
        emailVerificationRepository.save(verification);

        sendVerificationEmail(email, code);
    }

    // 회원가입(MemberService.signUp)의 트랜잭션 안에서 호출되더라도 코드 삭제만은 독립적으로 커밋되어야 하므로
    // REQUIRES_NEW로 별도 트랜잭션을 열고, 불일치/만료로 던지는 예외 때문에 그 삭제 자체가 롤백되지 않도록 한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = EmailVerificationException.class)
    public void verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new EmailVerificationException(EmailVerificationErrorCode.VERIFICATION_CODE_NOT_FOUND));

        boolean expired = verification.isExpired(CODE_TTL);
        boolean matched = verification.matches(code);
        // 대조된 순간 결과와 무관하게 삭제 (동일 코드 재사용/브루트포스 방지)
        emailVerificationRepository.delete(verification);

        if (expired) {
            throw new EmailVerificationException(EmailVerificationErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        if (!matched) {
            throw new EmailVerificationException(EmailVerificationErrorCode.VERIFICATION_CODE_MISMATCH);
        }
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, CODE_LENGTH);
        int code = secureRandom.nextInt(bound);
        return String.format("%0" + CODE_LENGTH + "d", code);
    }

    private void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[Sprint] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n10분 이내에 입력해주세요.");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("[ EmailVerificationService ]: 인증 메일 발송에 실패했습니다.", e);
            throw new EmailVerificationException(EmailVerificationErrorCode.MAIL_SEND_FAILED);
        }
    }

}

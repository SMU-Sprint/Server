package smu.sprint.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import smu.sprint.domain.member.entity.Member;
import smu.sprint.domain.member.repository.MemberRepository;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.security.auth.CustomUserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    public JwtUtil(@Value("${JWT_SECRET}") String secret,
                   @Value("${ACCESS_EXPIRATION_TIME}") Long accessExpMs,
                   @Value("${REFRESH_EXPIRATION_TIME}") Long refreshExpMs,
                   TokenRepository tokenRepository,
                   MemberRepository memberRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

//    public Roles getRoles(String token) throws SignatureException {
//        String roleStr = Jwts.parser()
//                .verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
//        try {
//            return Roles.valueOf(roleStr);
//        } catch (IllegalArgumentException | NullPointerException e) {
//            throw new SignatureException("유효하지 않은 Role값입니다.");
//        }
//    }

    public TokenType getTokenType(String token) throws SignatureException {
        String tokenType = Jwts.parser()
                .verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("tokenType", String.class);
        try {
            return TokenType.valueOf(tokenType);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SignatureException("유효하지 않은 토큰 종류입니다.");
        }
    }

    public String tokenProvider(CustomUserDetails customUserDetails, Instant expiration, TokenType tokenType) {
        Instant issuedAt = Instant.now();
        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .subject(customUserDetails.getUsername())
                .claim("role", authorities)
                .claim("tokenType", tokenType.name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createJwtAccessToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(customUserDetails, expiration, TokenType.ACCESS);
    }

    public String createJwtRefreshToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(customUserDetails, expiration, TokenType.REFRESH);
        // email을 기반으로 Member 불러오기
        Member member=  memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        // 기존에 저장된 토큰이 있으면 갱신하고, 없으면 새로 생성 (member_id가 PK이므로 insert 전용이면 재로그인 시 PK 충돌 발생)
        Token token = tokenRepository.findByMember(member)
                .map(existing -> {
                    existing.updateRefreshToken(refreshToken);
                    return existing;
                })
                .orElseGet(() -> Token.builder()
                        .member(member)
                        .refreshToken(refreshToken)
                        .build());
        // 토큰 저장
        tokenRepository.save(token);
        return refreshToken;
    }

    public JwtDTO reissueToken(String refreshToken) throws SignatureException {
        // validateToken을 먼저 거쳐야 위조/손상된 토큰이 getTokenType의 무방비 파싱 구간에서 처리되지 않은 예외로 터지지 않는다.
        validateToken(refreshToken);
        if (getTokenType(refreshToken) != TokenType.REFRESH) {
            throw new SignatureException("RefreshToken이 아닙니다.");
        }
        Member member = memberRepository.findByEmail(getEmail(refreshToken))
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Token savedToken = tokenRepository.findByMember(member)
                .orElseThrow(() -> new MemberException(MemberErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new SignatureException("유효하지 않은 RefreshToken입니다.");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(
                member.getEmail(),
                null,
                member.getRole()
        );
        log.info("[ JwtUtil ]: AccessToken과 RefreshToken을 재발급합니다.");
        // 재발급 시 RefreshToken도 함께 회전시켜 만료 기한을 최신으로 갱신 (createJwtRefreshToken이 upsert이므로 안전)
        return new JwtDTO(
                createJwtAccessToken(customUserDetails),
                createJwtRefreshToken(customUserDetails)
        );
    }

    public void invalidateRefreshToken(CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        // 이미 로그아웃되어 저장된 토큰이 없어도 에러로 취급하지 않고 그대로 종료 (멱등성)
        tokenRepository.findByMember(member).ifPresent(tokenRepository::delete);
        log.info("[ JwtUtil ]: RefreshToken을 폐기합니다.");
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String tokenFromHeader = request.getHeader("Authorization");
        if (tokenFromHeader == null || !tokenFromHeader.startsWith("Bearer ")) {
            return null;
        }
        // "Bearer " 뒤에 토큰이 없는 경우(공백만 있는 경우) split(" ")[1]이 ArrayIndexOutOfBoundsException을 던지므로 substring으로 안전하게 추출
        String token = tokenFromHeader.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }

    public void validateToken(String token) throws SignatureException {
        try {
            long seconds = 3 * 60;
            Jwts.parser()
                    .clockSkewSeconds(seconds)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰입니다.");
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // JwtAuthorizationFilter/AuthService가 catch하는 타입(java.security.SignatureException)과 맞춰야 하므로 io.jsonwebtoken 쪽 예외로 던지지 않는다.
            throw new SignatureException("잘못된 토큰입니다.", e);
        }
    }
}

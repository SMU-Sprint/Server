package smu.sprint.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import smu.sprint.domain.auth.dto.LoginRequest;
import smu.sprint.domain.auth.dto.LoginResponse;
import smu.sprint.domain.auth.dto.ReissueRequest;
import smu.sprint.global.code.AuthErrorCode;
import smu.sprint.global.exception.AuthException;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.jwt.JwtDTO;
import smu.sprint.global.security.jwt.JwtUtil;

import java.security.SignatureException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            // 이메일 미존재와 비밀번호 불일치를 구분하지 않고 동일하게 응답 (계정 열거 공격 방지)
            log.warn("[ AuthService ]: 로그인 실패 - email={}", request.email());
            throw new AuthException(AuthErrorCode.LOGIN_FAILED);
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw new AuthException(AuthErrorCode.LOGIN_FAILED);
        }
        JwtDTO token = new JwtDTO(
                jwtUtil.createJwtAccessToken(customUserDetails),
                jwtUtil.createJwtRefreshToken(customUserDetails)
        );

        log.info("[ AuthService ]: 로그인 성공 - email={}", customUserDetails.getUsername());
        return new LoginResponse(customUserDetails.getUsername(), token);
    }

    public JwtDTO reissue(ReissueRequest request) {
        try {
            return jwtUtil.reissueToken(request.refreshToken());
        } catch (ExpiredJwtException | SignatureException e) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public void logout(CustomUserDetails customUserDetails) {
        jwtUtil.invalidateRefreshToken(customUserDetails);
        log.info("[ AuthService ]: 로그아웃 - email={}", customUserDetails.getUsername());
    }

}

package smu.sprint.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import smu.sprint.domain.auth.dto.LoginRequest;
import smu.sprint.domain.auth.dto.LoginResponse;
import smu.sprint.global.code.MemberErrorCode;
import smu.sprint.global.exception.MemberException;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.jwt.JwtDTO;
import smu.sprint.global.security.jwt.JwtUtil;

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
            throw new MemberException(MemberErrorCode.LOGIN_FAILED);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        JwtDTO token = new JwtDTO(
                jwtUtil.createJwtAccessToken(customUserDetails),
                jwtUtil.createJwtRefreshToken(customUserDetails)
        );

        return new LoginResponse(customUserDetails.getUsername(), token);
    }

}

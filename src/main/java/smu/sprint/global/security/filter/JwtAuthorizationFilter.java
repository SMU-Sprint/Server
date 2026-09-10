package smu.sprint.global.security.filter;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.sprint.global.code.AuthErrorCode;
import smu.sprint.global.code.BaseErrorCode;
import smu.sprint.global.response.CustomResponse;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.auth.CustomUserDetailsService;
import smu.sprint.global.security.jwt.JwtUtil;
import smu.sprint.global.security.jwt.TokenType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.SignatureException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    protected void doFilterInternal (
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.equals("/favicon.ico") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("[ JwtAuthorizationFilter ]: 인가 필터 작동");

        try {
            String accessToken = jwtUtil.resolveAccessToken(request);
            if (accessToken == null) {
                log.info("[ JwtAuthorizationFilter ]: AccessToken이 존재하지 않습니다. 필터를 건너뜁니다.");
                filterChain.doFilter(request, response);
                return;
            }
            authenticateAccessToken(accessToken);
            log.info("[ JwtAuthorizationFilter ]: 다음 필터로 넘어갑니다.");
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("[ JwtAuthorizationFilter ]: 토큰이 만료되었습니다.");
            SecurityContextHolder.clearContext();
            writeErrorResponse(response, AuthErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            log.warn("[ JwtAuthorizationFilter ]: 유효하지 않은 토큰입니다. {}", e.getMessage());
            SecurityContextHolder.clearContext();
            writeErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
        }
    }

    // 클라이언트가 만료(재발급 필요)와 그 외 인증 실패(재로그인 필요)를 code로 구분할 수 있도록,
    // 다른 예외 처리 경로와 동일한 CustomResponse JSON 형식으로 응답한다.
    private void writeErrorResponse(HttpServletResponse response, BaseErrorCode errorCode) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        CustomResponse<Object> errorResponse = CustomResponse.onFailure(
                errorCode.getCode(), errorCode.getMessage(), null
        );
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }

    private void authenticateAccessToken(String accessToken) throws SignatureException {
        log.info("[ JwtAuthorizationFilter ]: 토큰으로 인가 과정을 시작합니다.");
        jwtUtil.validateToken(accessToken);
        log.info("[ JwtAuthorizationFilter ]: AccessToken 유효성 검증 성공");
        if (jwtUtil.getTokenType(accessToken) != TokenType.ACCESS) {
            throw new SignatureException("AccessToken이 아닙니다.");
        }
        String email = jwtUtil.getEmail(accessToken);
        CustomUserDetails customUserDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(email);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("[ JwtAuthorizationFilter ]: 인증 객체 저장 완료");
    }

}

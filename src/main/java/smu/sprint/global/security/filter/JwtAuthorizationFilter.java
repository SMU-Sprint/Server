package smu.sprint.global.security.filter;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.sprint.global.security.auth.CustomUserDetails;
import smu.sprint.global.security.auth.CustomUserDetailsService;
import smu.sprint.global.security.jwt.JwtUtil;
import smu.sprint.global.security.jwt.TokenType;

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
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("AccessToken이 만료되었습니다.");
        } catch (SignatureException e) {
            log.warn("[ JwtAuthorizationFilter ]: 유효하지 않은 토큰입니다. {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(e.getMessage());
        }
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

package smu.sprint.global.security.jwt;

public record JwtDTO(
        String jwtAccessToken,
        String jwtRefreshToken
) {
}

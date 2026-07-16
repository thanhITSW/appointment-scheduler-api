package com.appointment.security.jwt;

import com.appointment.entity.LoginSession;
import com.appointment.entity.User;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JwtTokenPayload {
    public static final String JWT_USER_ID = "userId";
    public static final String JWT_EMPLOYEE_ID = "employeeId";
    public static final String JWT_USERNAME = "username";
    public static final String JWT_ROLE = "role";
    public static final String JWT_TOKEN_ID = "tokenId";
    public static final String JWT_EXPIRATION = "expiration";
    public static final String JWT_IS_REFRESH = "isRefresh";

    private Long userId;
    private String username;
    private String employeeId;
    private String tokenId;
    private Date expiration;
    private Boolean isRefreshToken;
    private String role;

    public static JwtTokenPayload buildFromClaims(Claims claims) {
        return JwtTokenPayload.builder()
                .userId(claims.get(JWT_USER_ID, Long.class))
                .employeeId(claims.get(JWT_EMPLOYEE_ID, String.class))
                .username(claims.get(JWT_USERNAME, String.class))
                .tokenId(claims.getId())
                .expiration(claims.getExpiration())
                .role(claims.get(JWT_ROLE, String.class))
                .isRefreshToken(claims.get(JWT_IS_REFRESH, Boolean.class))
                .build();
    }

    public static JwtTokenPayload buildFormLoginSession(LoginSession session) {
        User user = session.getUser();
        if (user == null) {
            return null;
        }
        return JwtTokenPayload.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .employeeId(user.getEmployeeId())
                .tokenId(session.getSessionId())
                .expiration(Date.from(session.getExpiresAt()))
                .role(user.getRole().name())
                .isRefreshToken(false)
                .build();
    }
}

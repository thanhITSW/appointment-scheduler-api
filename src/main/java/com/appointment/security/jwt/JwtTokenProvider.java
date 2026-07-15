package com.appointment.security.jwt;

import com.appointment.config.ApplicationProperties;
import com.appointment.constant.CommonConstant;
import com.appointment.entity.User;
import com.appointment.enumeration.ClientType;
import com.appointment.enumeration.TokenValidationType;
import com.appointment.exception.BadRequestException;
import com.appointment.security.dto.JwtTokenDto;
import com.appointment.security.model.CustomUserDetail;
import com.appointment.service.CookieService;
import com.appointment.service.LoginSessionHandlerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final ApplicationProperties properties;
    private final CookieService cookieService;
    private final LoginSessionHandlerService loginSessionHandler;
    private JwtParser jwtParser;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = properties.getSecurity().getJwt().getSecret().getBytes();
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public JwtTokenDto generateLoginToken(CustomUserDetail authentication) {
        long now = System.currentTimeMillis();
        Long sessionExpirationConfig = properties.getSecurity().getJwt().getRenewTokenExpirationInMillis();
        Date tokenExpiration = new Date(now + properties.getSecurity().getJwt().getTokenExpirationInMillis());
        Date freshExpiration = new Date(now + sessionExpirationConfig);
        String tokenId = generateTokenId();
        return JwtTokenDto.builder()
                .username(authentication.getUsername())
                .employeeId(authentication.getEmployeeId())
                .expiredTime(freshExpiration.toInstant())
                .token(buildJwtToken(authentication, tokenExpiration, tokenId))
                .refreshToken(buildJwtRefreshToken(authentication, freshExpiration, tokenId))
                .userId(authentication.getUserId())
                .sessionId(tokenId)
                .build();
    }

    public JwtTokenDto generateUserLoginToken(User user) {
        return generateLoginToken(buildFromUser(user));
    }

    public JwtTokenDto generateFromToken(JwtTokenPayload tokenPayload) {
        long now = System.currentTimeMillis();
        Long sessionExpirationConfig = properties.getSecurity().getJwt().getRenewTokenExpirationInMillis();
        Date tokenExpiration = new Date(now + properties.getSecurity().getJwt().getTokenExpirationInMillis());
        Date freshExpiration = new Date(now + sessionExpirationConfig);
        return JwtTokenDto.builder()
                .username(tokenPayload.getUsername())
                .expiredTime(tokenExpiration.toInstant())
                .token(buildJwtToken(tokenPayload, tokenExpiration))
                .refreshToken(buildJwtRefreshToken(tokenPayload, freshExpiration))
                .userId(tokenPayload.getUserId())
                .sessionId(tokenPayload.getTokenId())
                .build();
    }

    private static String generateTokenId() {
        return UUID.randomUUID().toString();
    }

    public JwtTokenPayload parseJwtToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return JwtTokenPayload.buildFromClaims(claims);
        } catch (ExpiredJwtException e) {
            throw new BadRequestException("authentication.error.token-expired");
        } catch (Exception e) {
            throw new BadRequestException("authentication.error.token-invalid");
        }
    }

    public TokenValidationType validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return TokenValidationType.VALID;
        } catch (SignatureException e) {
            return TokenValidationType.INVALID_SIGNATURE;
        } catch (ExpiredJwtException e) {
            return TokenValidationType.EXPIRED;
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            return TokenValidationType.INVALID_FORMAT;
        }
    }

    private String buildJwtToken(CustomUserDetail user, Date expiration, String tokenId) {
        return Jwts.builder().setSubject(user.getUsername())
                .claim(JwtTokenPayload.JWT_USER_ID, user.getUserId())
                .claim(JwtTokenPayload.JWT_ROLE, user.getRole())
                .claim(JwtTokenPayload.JWT_TOKEN_ID, tokenId)
                .setId(tokenId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiration)
                .compact();
    }

    private String buildJwtRefreshToken(CustomUserDetail user, Date expiration, String tokenId) {
        return Jwts.builder().setSubject(user.getUsername())
                .claim(JwtTokenPayload.JWT_USER_ID, user.getUserId())
                .claim(JwtTokenPayload.JWT_EMPLOYEE_ID, user.getEmployeeId())
                .claim(JwtTokenPayload.JWT_ROLE, user.getRole())
                .claim(JwtTokenPayload.JWT_EXPIRATION, expiration)
                .claim(JwtTokenPayload.JWT_IS_REFRESH, true)
                .claim(JwtTokenPayload.JWT_TOKEN_ID, tokenId)
                .setId(tokenId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiration)
                .compact();
    }

    private String buildJwtToken(JwtTokenPayload payload, Date expiration) {
        return Jwts.builder().setSubject(payload.getUsername())
                .claim(JwtTokenPayload.JWT_USER_ID, payload.getUserId())
                .claim(JwtTokenPayload.JWT_EMPLOYEE_ID, payload.getEmployeeId())
                .claim(JwtTokenPayload.JWT_ROLE, payload.getRole())
                .claim(JwtTokenPayload.JWT_EXPIRATION, expiration)
                .claim(JwtTokenPayload.JWT_TOKEN_ID, payload.getTokenId())
                .setId(payload.getTokenId())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiration)
                .compact();
    }

    private String buildJwtRefreshToken(JwtTokenPayload payload, Date expiration) {
        return Jwts.builder().setSubject(payload.getUsername())
                .claim(JwtTokenPayload.JWT_USER_ID, payload.getUserId())
                .claim(JwtTokenPayload.JWT_EMPLOYEE_ID, payload.getEmployeeId())
                .claim(JwtTokenPayload.JWT_ROLE, payload.getRole())
                .claim(JwtTokenPayload.JWT_EXPIRATION, expiration)
                .claim(JwtTokenPayload.JWT_IS_REFRESH, true)
                .claim(JwtTokenPayload.JWT_TOKEN_ID, payload.getTokenId())
                .setId(payload.getTokenId())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiration)
                .compact();
    }

    private CustomUserDetail buildFromUser(User user) {
        return CustomUserDetail.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .employeeId(user.getEmployeeId())
                .password(user.getPassword())
                .role(user.getRole().name())
                .build();
    }

    public String generateAccessToken(User user) {
        long now = System.currentTimeMillis();
        Date tokenExpiration = new Date(now + properties.getSecurity().getJwt().getTokenExpirationInMillis());
        String tokenId = generateTokenId();
        
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(JwtTokenPayload.JWT_USER_ID, user.getId())
                .claim(JwtTokenPayload.JWT_EMPLOYEE_ID, user.getEmployeeId())
                .claim(JwtTokenPayload.JWT_ROLE, user.getRole())
                .claim(JwtTokenPayload.JWT_TOKEN_ID, tokenId)
                .setId(tokenId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(tokenExpiration)
                .compact();
    }

    public String generateRefreshToken(User user) {
        long now = System.currentTimeMillis();
        Long sessionExpirationConfig = properties.getSecurity().getJwt().getRenewTokenExpirationInMillis();
        Date freshExpiration = new Date(now + sessionExpirationConfig);
        String tokenId = generateTokenId();
        
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(JwtTokenPayload.JWT_USER_ID, user.getId())
                .claim(JwtTokenPayload.JWT_EMPLOYEE_ID, user.getEmployeeId())
                .claim(JwtTokenPayload.JWT_ROLE, user.getRole())
                .claim(JwtTokenPayload.JWT_EXPIRATION, freshExpiration)
                .claim(JwtTokenPayload.JWT_IS_REFRESH, true)
                .claim(JwtTokenPayload.JWT_TOKEN_ID, tokenId)
                .setId(tokenId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(freshExpiration)
                .compact();
    }

    public ClientType getClientType(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-Client-Type"))
                .map(String::toUpperCase)
                .map(ClientType::valueOf)
                .orElse(ClientType.WEB);
    }

    public Optional<JwtTokenPayload> getTokenDetail(HttpServletRequest request, ClientType clientType) {
        if (ClientType.MOBILE == clientType) {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                return loginSessionHandler.extractCacheLoginSession(bearerToken.substring(7));
            }
        } else {
            return cookieService.getCookieByName(request, CommonConstant.TOKEN_COOKIE_NAME)
                    .map(Cookie::getValue)
                    .flatMap(loginSessionHandler::extractCacheLoginSession);
        }

        return Optional.empty();
    }

}

package com.Kidari.server.config.jwt;

import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.TokenException;
import com.Kidari.server.domain.member.entity.MemberDto;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${secret.time.access}")
    private long accessTokenTime; // 30분;
    @Value("${secret.time.refresh}")
    private long refreshTokenTime; // 14일;
    @Value("${secret.key}")
    private String jwtSecretKey;
    private final StringRedisTemplate stringRedisTemplate;

    public ResponseCookie createResponseCookie(String title, String token) {
        return ResponseCookie.from(title, token)
                .path("/")
                .secure(true)
                .sameSite("None")
                .build();
    }

    public String createAccessToken(MemberDto member) {
        Claims claims = Jwts.claims();
        claims.put("sub", member.getSub());
        claims.put("role", member.getRole().getRole());
        long validTime = accessTokenTime;
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }


    public String createRefreshToken(MemberDto member) {
        Claims claims = Jwts.claims();
        claims.put("sub", member.getSub());
        long validTime = refreshTokenTime;
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();

        updateUserRefreshToken(member, refreshToken);

        return refreshToken;
    }

    public void updateUserRefreshToken(MemberDto member, String refreshToken) {
        stringRedisTemplate.opsForValue().set(member.getSub(), refreshToken, refreshTokenTime, TimeUnit.MILLISECONDS);
    }

    public String getUserRefreshToken(String nickname) {
        return stringRedisTemplate.opsForValue().get(nickname);
    }

    public void deleteRefreshTokenByGitHubSub(String sub) {
        if (getUserRefreshToken(sub) != null) {
            stringRedisTemplate.delete(sub);
        }
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
//            throw new AppException(ErrorCode.JWT_TOKEN_NOT_EXISTS);
            throw new TokenException(ErrorCode.JWT_TOKEN_NOT_EXISTS);
        }
        if(isLogout(token)){
//            throw new AppException(ErrorCode.JWT_TOKEN_EXPIRED);
            throw new TokenException(ErrorCode.LOG_OUT_JWT_TOKEN);
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
            log.info("token \"sub\" : " + claims.get("sub"));
            log.info("token \"role\" : " + claims.get("role"));
            return true;
        } catch (MalformedJwtException e) {
//            throw new AppException(ErrorCode.WRONG_JWT_TOKEN);
            throw new TokenException(ErrorCode.WRONG_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
//            throw new AppException(ErrorCode.JWT_TOKEN_EXPIRED);
            throw new TokenException(ErrorCode.JWT_TOKEN_EXPIRED);
        }
    }

    public Authentication getAuthentication(String token) {
        // 토큰 복호화
        Claims claims = getClaims(token);

        if (claims.get("role") == null) {
            throw new TokenException(ErrorCode.WRONG_JWT_TOKEN);
        }

        // 클레임에서 권한 정보 취득
        String login = getGitHubLoginFromToken(token);
        String role = getRoleFromToken(token);

        // UserDetails 객체를 생성하여 Authentication 반환
        UserDetails principal = new User(getGitHubSubFromToken(token), login, Collections.singleton(new SimpleGrantedAuthority(role)));
        return new UsernamePasswordAuthenticationToken(principal, "", Collections.singleton(new SimpleGrantedAuthority(role)));
    }

    public void setBlackList(String accessToken) {
        Long expiration = getExpiration(accessToken);
        stringRedisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isLogout(String accessToken) {
        return !ObjectUtils.isEmpty(stringRedisTemplate.opsForValue().get(accessToken));
    }

    public Long getExpiration(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.getTime() - new Date().getTime();
    }

    public String getGitHubSubFromToken(String token) {
        return getClaims(token).get("sub").toString();
    }

    public String getGitHubLoginFromToken(String token) {
        return getClaims(token).get("login").toString();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role").toString();
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
    }

    public String resolveJWT(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }
}

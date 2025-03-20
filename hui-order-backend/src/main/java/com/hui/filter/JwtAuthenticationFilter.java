package com.hui.filter;

import com.hui.service.UserService;
import com.hui.entity.User;
import com.hui.util.JwtTokenProvider;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JWT Authentication Filter
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 獲取請求路徑
            String requestPath = request.getRequestURI();
            logger.info("Processing request for path: {}", requestPath);

            // 非管理員路徑，嘗試進行一般認證
            String token = extractJwtFromRequest(request);

            if (token != null) {
                try {
                    Claims claims = jwtTokenProvider.parseToken(token);

                    if (claims != null) {
                        Map<String, Object> userData = JSON.parseObject(claims.getSubject(), Map.class);
                        String username = (String) userData.get("username");
                        String role = (String) userData.get("role");

                        if (role == null) {
                            role = "USER";
                        }

                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority(role));

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, authorities
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    logger.error("Error processing token for non-admin path: {}", e.getMessage());
                    // 不是管理員路徑，不需要中斷，繼續處理
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 處理過期令牌
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has expired");
        } catch (MalformedJwtException e) {
            // 處理格式錯誤的令牌
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token format");
        } catch (UnsupportedJwtException e) {
            // 處理不支持的令牌
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unsupported token");
        } catch (Exception e) {
            // 處理其他所有異常
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * 從請求標頭中提取 JWT 令牌
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // 首先檢查 Authorization 標頭
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 也可以檢查自定義標頭
        String customToken = request.getHeader("x-auth-token");
        if (StringUtils.hasText(customToken)) {
            return customToken;
        }

        return null;
    }
}

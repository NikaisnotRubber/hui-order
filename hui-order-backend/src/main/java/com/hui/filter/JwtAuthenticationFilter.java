package com.hui.filter;

import com.hui.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 從請求頭中獲取 JWT
        String token = extractJwtFromRequest(request);

        // 如果沒有令牌，繼續過濾鏈
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 解析令牌
            Claims claims = jwtTokenProvider.parseToken(token);

            // 如果令牌有效，設置 Spring Security 的認證信息
            if (claims != null) {
                Integer userId = jwtTokenProvider.getUserIdFromToken(token);

                // 創建認證信息（這裡僅使用用戶 ID 和一個基本角色）
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                
                // 將認證信息添加到當前線程的 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
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
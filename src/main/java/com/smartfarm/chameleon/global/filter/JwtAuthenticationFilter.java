package com.smartfarm.chameleon.global.filter;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.global.jwt.CustomUserDetailsService;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;
import com.smartfarm.chameleon.global.redis.RedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RedisService redisService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.redisService = redisService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("filter 거치는 중입니다.");
        log.info("Received request for URI: {}", request.getRequestURI());

        // login api라면 filter를 수행하지 않고 넘김
        if ("/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // request에서 token 불러오기
        String access_token = resolveAccessToken(request);
        String refresh_token = resolveRefreshToken(request);
        
        // access_token이 존재 && access_token 기본검증+유효기간 검증을 통과
        if (access_token != null && jwtTokenProvider.validateToken(access_token)){

            log.info("access_token 검증 통과");

            // 사용자 아이디
            String userID = jwtTokenProvider.getUserID(access_token);

            // 사용자 아이디를 통해 UserDetail 생성
            UserDetails userDetails = userDetailsService.loadUserByUsername(userID);

            if(userDetails != null){

                // 사용자의 세부 정보와 권한
                UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Security Context에 저장            
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }else{

            log.info("access_token 검증 실패");

            // refresh_token이 존재 && 기본검증+유효기간 검증 통과 && Redis에 refresh_token이 존재
            if (refresh_token != null && jwtTokenProvider.validateToken(refresh_token) 
                    && redisService.getData(jwtTokenProvider.getUserID(refresh_token)) != null ){

                log.info("refresh token 검증 통과");

                // 사용자 아이디
                String userID = jwtTokenProvider.getUserID(refresh_token);
                TokenDTO new_access_token = jwtTokenProvider.createAccessToken(userID);

                log.info("새로운 access_token이 발급되었습니다.");
                response.setContentType("application/json");
                response.getWriter().write("{\"new_access_token\": \"" + new_access_token.getAccess_token() + "\"}");
                response.getWriter().flush();
                return;

            }else{
                // 토큰이 만료되었을 경우
                log.warn("token이 만료되었습니다!");

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
                response.getWriter().write("token is expired or invalid.");
                response.getWriter().flush();
                return;
            }
        }

        log.info("next filter");

        // 다음 필터로 넘기기
        filterChain.doFilter(request, response);
                
    }

    // header에서 access_token 불러오기
    private String resolveAccessToken(HttpServletRequest request) {

        // bearer이 붙어있는 token
        String bearerToken = request.getHeader("Authorization");

        // bearer값을 제외한 token값만을 전달
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // header에서 refresh_token 불러오기
    private String resolveRefreshToken(HttpServletRequest request) {

        // bearer이 붙어있는 token
        String bearerToken = request.getHeader("REFRESH_TOKEN");

        // bearer값을 제외한 token값만을 전달
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
}

package com.smartfarm.chameleon.global.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.global.config.SecurityConfig;
import com.smartfarm.chameleon.global.jwt.CustomUserDetail;
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
    public static final List<String> PUBLIC_URIS_EQUAL = SecurityConfig.PUBLIC_URIS_EQUAL;
    public static final List<String> PUBLIC_URIS_START = SecurityConfig.PUBLIC_URIS_START;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.redisService = redisService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("JwtAuthenticationFilter : filter 거치는 중입니다.");
        log.debug("JwtAuthenticationFilter : Received request for URI - {}", request.getRequestURI());

        // login 또는 회원가입, swagger라면 filter를 수행하지 않고 넘김
        if (PUBLIC_URIS_EQUAL.contains(request.getRequestURI()) ||
                PUBLIC_URIS_START.stream().anyMatch( uri -> request.getRequestURI().startsWith(uri)) ) {
            log.info("JwtAuthenticationFilter : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // request에서 token 불러오기
            String access_token = resolveAccessToken(request);
            String refresh_token = resolveRefreshToken(request);

            // access token과 refresh token이 둘다 존재하지 않을 때 바로 예외 발생
            if(access_token == null && refresh_token == null){
                log.info("JwtAuthenticationFilter : access_token과 refresh_token 모두 존재 X");
                throw new Exception("access_token과 refresh_token이 존재하지 않습니다.");
            }
            
            //access_token 기본검증+유효기간 검증을 통과
            if (jwtTokenProvider.validateToken(access_token)){

                log.info("JwtAuthenticationFilter : access_token 검증 통과");
                    
                // 사용자 아이디
                String userID = jwtTokenProvider.getUserID(access_token);

                // 사용자 아이디를 통해 UserDetail 생성
                CustomUserDetail userDetails = userDetailsService.loadUserByUsername(userID);

                if(userDetails != null){

                    // 사용자의 세부 정보와 권한
                    UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    // Security Context에 저장            
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("JwtAuthenticationFilter : next filter");
                    // 다음 필터로 넘기기
                    filterChain.doFilter(request, response);    
                }

            }else{

                    log.info("JwtAuthenticationFilter : access_token 검증 실패");

                    // log.info("refresh_token : " + refresh_token);
                    // log.info("refresh_token 검증 결과 : " + jwtTokenProvider.validateToken(refresh_token));
                    // log.info("refresh_token이 redis에 있는지 : " + redisService.getData(jwtTokenProvider.getUserID(refresh_token)) );

                    // 기본검증+유효기간 검증 통과 && Redis에 refresh_token이 존재
                    if (jwtTokenProvider.validateToken(refresh_token) 
                            && redisService.getData(jwtTokenProvider.getUserID(refresh_token)) != null ){

                        log.info("JwtAuthenticationFilter : refresh token 검증 통과");

                        // 사용자 아이디
                        String userID = jwtTokenProvider.getUserID(refresh_token);
                        TokenDTO new_access_token = jwtTokenProvider.createAccessToken(userID);

                        log.info("JwtAuthenticationFilter : 새로운 access_token이 발급되었습니다.");
                        sendToClient(response, new_access_token.getAccess_token(), false);

                    }else{
                        // 토큰이 만료되었을 경우
                        log.info("JwtAuthenticationFilter : access token과 refresh token이 만료되었습니다!");
                        throw new Exception("access token과 refresh token이 모두 만료되었습니다. 다시 로그인 해주세요.");
                    }

            }

        } catch (Exception e) {
            sendToClient(response, e.getMessage(), true);
        }
                
    }

    /**
     * 사용자에게 응답을 보내는 메서드
     * 
     * @param response : response 객체
     * @param Message : 사용자에게 전달할 메시지 (오류 메시지 or 새로운 access token)
     * @param isError : 오류 여부 (true - 오류 / false - 새로운 access token)
     */
    private void sendToClient(HttpServletResponse response, String Message, boolean isError){
        try {

            if(isError){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
                response.setContentType("text/plain;charset=UTF-8" );
                response.getWriter().write(Message);
                response.getWriter().flush();
                return;
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
                response.setContentType("application/json");
                response.getWriter().write("{\"new_access_token\": \"" + Message + "\"}");
                response.getWriter().flush();
                return;
            }

        } catch (Exception e) {
            log.info("JwtAuthenticationFilter - sendToClient : {} : {}", e.getClass() , e.getMessage());
        }
    }

    // header에서 access_token 불러오기
    private String resolveAccessToken(HttpServletRequest request){

        // bearer이 붙어있는 token
        String bearerToken = request.getHeader("Authorization");

        // bearer값을 제외한 token값만을 전달
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // header에서 refresh_token 불러오기
    private String resolveRefreshToken(HttpServletRequest request){

        // bearer이 붙어있는 token
        String bearerToken = request.getHeader("REFRESH_TOKEN");

        // bearer값을 제외한 token값만을 전달
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
}

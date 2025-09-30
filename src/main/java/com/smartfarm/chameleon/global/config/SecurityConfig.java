package com.smartfarm.chameleon.global.config;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.smartfarm.chameleon.global.filter.JwtAuthenticationFilter;
import com.smartfarm.chameleon.global.jwt.CustomUserDetailsService;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;
import com.smartfarm.chameleon.global.redis.RedisService;

import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RedisService redisService;

    // 완전히 일치해야하는 URI 목록
    public static final List<String> PUBLIC_URIS_EQUAL = List.of(
        "/login", "/user/serial", "/user/sign_up"
    );
    // 패턴에 맞춰 시작만 일치해도 되는 URI 목록
    public static final List<String> PUBLIC_URIS_START = List.of(
        "/swagger-ui/", "/swagger-ui/**",
        "/api-docs", "/api-docs/**", "/v3/api-docs/**"
    );


    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.disable())  // basic auth 사용 X
                .csrf(csrf -> csrf.disable())       // csrf 보안을 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(Stream.concat(PUBLIC_URIS_EQUAL.stream(), PUBLIC_URIS_START.stream())
                                        .toArray(String[]::new)).permitAll() // 로그인 API는 인증 없이 접근 가능
                                .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, redisService),
                        UsernamePasswordAuthenticationFilter.class)
                .cors(withDefaults());    // cors 활성화와 동시에 WebConfig 옵션을 사용
        
        return http.build();
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
}

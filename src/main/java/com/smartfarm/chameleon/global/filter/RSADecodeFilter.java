package com.smartfarm.chameleon.global.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartfarm.chameleon.global.rsa.DecodeRequestWrapper;
import com.smartfarm.chameleon.global.rsa.RsaKeyHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RSADecodeFilter extends OncePerRequestFilter {
    
    
    private final RsaKeyHolder rsaKeyHolder;

    public RSADecodeFilter(RsaKeyHolder rsaKeyHolder){
        this.rsaKeyHolder = rsaKeyHolder;
    }

    // 개인정보가 포함된 API
    public static final List<String> USER_INFO_API = List.of(
        "/login", "/user/serial", "/user/sign_up", "/user/update", "/fcm/send_message", "/fcm/update_fcm_token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        

        // 개인정보가 포함된 API가 아니라면 넘긴다.
        if(!USER_INFO_API.contains(request.getRequestURI())){

            log.info("개인정보가 포함된 API가 아닙니다.");

            filterChain.doFilter(request, response);
            return;
        }
				

        log.info("복호화 단계로 넘어갑니다.");
        // DecodeRequestWrapper에 
        filterChain.doFilter(new DecodeRequestWrapper(request, rsaKeyHolder), response);
    }


}

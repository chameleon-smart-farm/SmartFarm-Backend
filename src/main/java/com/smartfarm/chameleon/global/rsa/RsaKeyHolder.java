package com.smartfarm.chameleon.global.rsa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Component 
public class RsaKeyHolder {
    
    public PrivateKey PRIVATE_KEY;

    public RsaKeyHolder (@Value("${rsa.private-key.filename}") String privateKeyFile){

        try {
            // 파일에서 비밀 키를 불러옴
            ClassPathResource privateKeyResource = new ClassPathResource(privateKeyFile);
            String stringKey = new BufferedReader(new InputStreamReader(privateKeyResource.getInputStream()))
                            .lines().collect(Collectors.joining("\n"));
                            
            stringKey = stringKey 
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "")
                .replaceAll("\\n", "");
            
            // 비밀 키를 PrivateKey 객체로 변환
            byte[] decoded = Base64.getDecoder().decode(stringKey);
            PRIVATE_KEY = KeyFactory.getInstance("RSA")
                        .generatePrivate(new PKCS8EncodedKeySpec(decoded));

        } catch (Exception e) {
            log.error("에러 발생으로 비밀 키 생성 실패 : {}", e);
        }

    }

}

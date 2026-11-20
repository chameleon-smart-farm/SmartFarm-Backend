package com.smartfarm.chameleon.global.rsa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.io.IOUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecodeRequestWrapper extends HttpServletRequestWrapper {

    // 여러번 읽을수 있게 복호화한 내용을 바이트로 들고 있는다
    private byte[] body;

    public DecodeRequestWrapper(HttpServletRequest request, RsaKeyHolder rsaKeyHolder) {
        super(request);
        
        try {
            // request의 body 추출, 이후 원래 스트림을 다시 읽을 수 없음
            byte[] raw = IOUtils.toByteArray(request.getInputStream());
            String encrypted = new String(raw, StandardCharsets.UTF_8).replace("\"", "");
            byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);

		    // 복호화
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, rsaKeyHolder.PRIVATE_KEY);
			byte[] decrypted = cipher.doFinal(encryptedBytes);
			this.body = decrypted;
				    
            log.info("복호화 성공");

        } catch (Exception e) {
            log.error("복호화 실패 : {}", e);
        }
    }


    // @RequestBody 가 부르는 메서드다. 부를때마다 새 스트림을 준다
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }


    
}

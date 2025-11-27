package com.smartfarm.chameleon.global.config;

import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FCMConfig {

    @PostConstruct
    public void initializeFCM() {
        try {
            
            InputStream serviceAccount = new ClassPathResource("chameleon-android-df69c-firebase-adminsdk-fbsvc-ec632eb126.json").getInputStream();
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            // FirebaseApp이 초기화되어 있지 않은 경우에만 초기화 실행              
            if (FirebaseApp.getApps().isEmpty()) { 
                FirebaseApp.initializeApp(options);
            }

        } catch (Exception e) {
            log.error("FirebaseConfig 에러 : " + e.getMessage());
        }
    }
    
}

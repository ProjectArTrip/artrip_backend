package org.atdev.artrip.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Component
public class FCMConfig {

    @Value("${firebase.credentials.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        log.info("[Firebase] Initialization started");
        if (FirebaseApp.getApps().isEmpty()) {

            try (FileInputStream serviceAccount =
                         new FileInputStream(firebaseConfigPath)) {

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        }
    }

}

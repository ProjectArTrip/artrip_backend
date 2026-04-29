package org.atdev.artrip.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@EnableConfigurationProperties(FcmProperties.class)
public class FcmConfig {

    private final ClassPathResource firebaseResource;
    private final String projectId;

    @Value("${notification.fcm.http-trace}")
    private boolean httpTrace;

    public FcmConfig(FcmProperties fcmProperties) {
        this.firebaseResource = new ClassPathResource(fcmProperties.fcm().filePath());
        this.projectId = fcmProperties.fcm().projectId();
    }

    @PostConstruct
    public void init() throws IOException {
        FirebaseOptions option = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
                .setProjectId(projectId)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(option);
        }

        if(httpTrace) {
            Logger httpsLogger = Logger.getLogger("com.google.api.client.http.HttpTransport");
            httpsLogger.setLevel(Level.CONFIG);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.CONFIG);
            httpsLogger.addHandler(handler);
            httpsLogger.setUseParentHandlers(false);
        }
    }


    @Bean
    FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance(firebaseApp());
    }

    @Bean
    FirebaseApp firebaseApp() {
        return FirebaseApp.getInstance();
    }

    @Bean(name = "fcmExecutor")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("FcmExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();
        return executor;
    }
}

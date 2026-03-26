package org.atdev.artrip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
public record FcmProperties(
        String title,
        String body,
        Fcm fcm
) {

    public record Fcm(
            String filePath,
            String projectId
    ) {
    }
}

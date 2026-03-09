package org.atdev.artrip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring")
public record FcmProperties(
        String title,
        String body,
        Fcm fcm
) {

    public record Fcm(
            String file_path,
            String project_id
    ) {
    }
}

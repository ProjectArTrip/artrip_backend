package org.atdev.artrip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "maintenance.access")
public record MaintenanceAccessProperties(
        List<String> allowedPaths,
        List<String> allowedIps
) {
}

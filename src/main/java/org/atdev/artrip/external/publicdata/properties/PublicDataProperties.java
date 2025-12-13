package org.atdev.artrip.external.publicdata.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external.publicdata")
public class PublicDataProperties {

    private String baseUrl;
    private String serviceKey;
    private int connectTimeout = 5000;
    private int readTimeout = 30000;
    private int pageSize = 100;
}

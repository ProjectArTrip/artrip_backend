package org.atdev.artrip.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {

    private boolean secure;
    private String sameSite;

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }

    @PostConstruct
    void validate() {
        if ("None".equalsIgnoreCase(sameSite) && !secure) {
            throw new IllegalStateException("app.cookie.same-site=None requires app.cookie.secure=true");
        }
    }
}

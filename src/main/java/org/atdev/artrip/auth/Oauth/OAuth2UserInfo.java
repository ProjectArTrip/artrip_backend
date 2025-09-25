package org.atdev.artrip.auth.Oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    public abstract String getId();
    public abstract String getName();

    public abstract String getNameAttributeKey();

    public abstract String getEmail();

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
package org.atdev.artrip.domain.oauth;

import java.util.Map;


public class GoogleUserInfo extends OAuth2UserInfo {

    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object id = attributes.get("sub");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getName() {
        return (String) attributes.getOrDefault("name", null);
    }

    @Override
    public String getEmail() {
        return (String) attributes.getOrDefault("email", null);
    }

    @Override
    public String getNameAttributeKey() {
        return "sub";
    }
}

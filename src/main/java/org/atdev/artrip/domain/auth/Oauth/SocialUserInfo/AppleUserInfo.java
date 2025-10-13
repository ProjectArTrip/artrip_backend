package org.atdev.artrip.domain.auth.Oauth.SocialUserInfo;

import org.atdev.artrip.domain.auth.Oauth.OAuth2UserInfo;
import java.util.Map;


public class AppleUserInfo extends OAuth2UserInfo {

    public AppleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object id = attributes.get("sub");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getName() {
        Map<String, Object> name = (Map<String, Object>) attributes.get("name");
        if (name == null) return null;
        String firstName = (String) name.get("firstName");
        String lastName = (String) name.get("lastName");
        if (firstName == null && lastName == null) return null;
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
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
package com.lsb.kkirikkiri.configs.oauth2;

import javax.naming.spi.ObjectFactory;
import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getSocialId();
    public abstract String getEmail();
    public abstract String getNickname();
    public abstract String getProfileImage();
}

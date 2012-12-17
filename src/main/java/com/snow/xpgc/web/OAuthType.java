package com.snow.xpgc.web;


import org.scribe.builder.api.*;

public enum OAuthType {
    FB("facebook", FacebookApi.class), GH("github", GitHubApi.class),
    GOOGLE("google", GoogleApi20.class), LINKEDIN("linkedin", LinkedInApi.class);
    private String key;
    private Class<? extends Api> apiClass;

    private OAuthType(String key, Class<? extends Api> apiClass) {
        this.key = key;
        this.apiClass = apiClass;
    }

    public String getKey() {
        return key;
    }

    public Class<? extends Api> getApiClass() {
        return apiClass;
    }
}
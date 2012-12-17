package com.snow.xpgc;


import com.britesnow.snow.web.auth.AuthRequest;
import com.google.inject.AbstractModule;
import com.snow.xpgc.web.GoogleAuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XpGCConfig extends AbstractModule {
    private static Logger log = LoggerFactory.getLogger(XpGCConfig.class);

    @Override
    protected void configure() {
        bind(AuthRequest.class).to(GoogleAuthRequest.class);
    }
}

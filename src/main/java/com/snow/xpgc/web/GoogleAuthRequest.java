package com.snow.xpgc.web;

import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.auth.AuthRequest;
import com.britesnow.snow.web.auth.AuthToken;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;


@Singleton
public class GoogleAuthRequest implements AuthRequest {
    public static final String TOKEN = "google_token";

    private static Logger log = LoggerFactory.getLogger(GoogleAuthRequest.class);
    private final OAuthService googleService;


    @Inject
    public GoogleAuthRequest(OAuthUtils oAuthUtils) {
        googleService = oAuthUtils.getOauthService(OAuthType.GOOGLE);
    }


    @Override
    public AuthToken authRequest(RequestContext rc) {
        // Note: this is not the login logic, the login logic would be
        // @WebActionHandler that would generate the appropriate

        // Note: this is a simple stateless authentication scheme.
        // Security is medium-low, however, with little bit more logic
        // it can be as secure as statefull login while keeping it's scalability attributes

        // First, we get userId and userToken from cookie
        String googleToken = rc.getCookie(TOKEN);

        if (googleToken != null ) {
            // get the User from the DAO
            AuthToken result = new AuthToken();
            result.setUser(googleToken);
            return result;
        } else {
            return null;
        }
    }

    @WebModelHandler(startsWith = "/")
    public void pageIndex(@WebModel Map m, @WebUser String token, RequestContext rc) {
        if (token != null) {
            m.put("token", token);
        }

    }



    @WebModelHandler(startsWith = "/googleLogin")
    public void googleLogin(RequestContext rc) throws IOException {
        String url = googleService.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println(url);
        rc.getRes().sendRedirect(url);
    }

    @WebModelHandler(startsWith = "/googleCallback")
    public void googleCallback(RequestContext rc, @WebParam("code") String code) throws Exception {
        if (code != null) {
            Verifier verifier = new Verifier(code);
            Token accessToken = googleService.getAccessToken(EMPTY_TOKEN, verifier);
            if (accessToken.getToken() != null) {
                Cookie cookie = new Cookie(TOKEN, accessToken.getToken());
                cookie.setMaxAge(-1);
                rc.getRes().addCookie (cookie);
                rc.getRes().sendRedirect(rc.getReq().getContextPath());
            }else{
                googleLogin(rc);
            }

        }
    }

}
package br.com.unopay.api.uaa.oauth2;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

public class AuthUserContextHolder {

    private static final String AUTH_USER_ID = "AUTH_USER_ID";

    private AuthUserContextHolder() {
        throw new IllegalAccessError("Utility class");
    }

    public static void setAuthUserId(String userId) {
        currentRequestAttributes().setAttribute(AUTH_USER_ID, userId, SCOPE_REQUEST);
    }

    public static String getAuthUserId() {
        return (String) currentRequestAttributes().getAttribute(AUTH_USER_ID, SCOPE_REQUEST);
    }

}
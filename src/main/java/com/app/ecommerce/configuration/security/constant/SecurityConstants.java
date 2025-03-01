package com.app.ecommerce.configuration.security.constant;

public class SecurityConstants {
    private SecurityConstants() {
    }
    public static final String ACCESS_KEY = "qSdEewYUdG3nQVGjUCqchCNxHysymkMFGTHSKRETO($GKqrfTXUEy6CmmgBXESHKHK";
    public static final String REFRESH_KEY = "nsfjshufaueuw9-r0oekmku3yj3n4mreo-dsMFSE)$dlljrih#%#RKGGrrko#%,glkdek^Yh";
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_WS_HEADER = "Cookie";
    public static final String REFRESH_TOKEN_ATT = "refresh_token";
    public static final long ACCESS_TOKEN_DURATION = 7 * 24 * 60 * 60 * 1000L;
    public static final long REFRESH_TOKEN_DURATION = 30 * 24 * 60 * 60 * 1000L;
}

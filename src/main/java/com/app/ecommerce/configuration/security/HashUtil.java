package com.app.ecommerce.configuration.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class HashUtil {
    public String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}

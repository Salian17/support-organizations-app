package com.example.supportorganizationsapp.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public String getPrincipalEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() ||
//                "anonymousUser".equals(auth.getPrincipal())) {
//            throw new ResourceNotFoundException("User isn't authenticated");
//        }
        return auth.getName();
    }
}


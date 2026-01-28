package com.timetracking.timesheet.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String principal) {
            try {
                return UUID.fromString(principal);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}

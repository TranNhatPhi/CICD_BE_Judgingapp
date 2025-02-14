package com.project.judging.Constant;

import org.springframework.stereotype.Component;

public class RoleValiddator {

    public RoleValiddator() {
    }

    public static String roleString(String role) {
        if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Judge")) {
            return role.toLowerCase();
        } else {
            throw new IllegalArgumentException("Invalid role");
        } 
    }
}
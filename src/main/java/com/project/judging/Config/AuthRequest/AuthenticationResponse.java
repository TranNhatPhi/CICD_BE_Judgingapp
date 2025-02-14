package com.project.judging.Config.AuthRequest;

public class AuthenticationResponse {

    private final String jwt;
    private final Integer id;
    private final String username;
    private final Integer semesterId;
    private final String role;

    public AuthenticationResponse(String jwt, Integer id, String username, Integer semesterId, String role) {
        this.jwt = jwt;
        this.id = id;
        this.username = username;
        this.semesterId = semesterId;
        this.role = role;
    }

    public String getJwt() {
        return jwt;
    }

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public Integer getSemesterId() { return semesterId; }
}

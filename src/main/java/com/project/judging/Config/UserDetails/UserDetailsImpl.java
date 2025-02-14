package com.project.judging.Config.UserDetails;

import com.project.judging.Entities.Judge;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    // Implement methods from UserDetails interface
    @Getter
    private Integer id;

    private final String username;

    @JsonIgnore
    private String password;

    @Getter
    private final Integer semesterId;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String username, String password, Integer semesterId, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.semesterId = semesterId;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Judge judge) {
        Integer semesterId = judge.getSemester() != null ? judge.getSemester().getId() : null;
        return new UserDetailsImpl(
                judge.getId(),
                judge.getAccount(),
                judge.getPwd(),
                semesterId,
                Collections.singletonList(new SimpleGrantedAuthority(judge.getRole()))
        );
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

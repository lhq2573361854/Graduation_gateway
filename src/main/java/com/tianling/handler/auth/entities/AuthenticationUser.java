package com.tianling.handler.auth.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/2 15:37
 */
@Getter
@Setter
public class AuthenticationUser  extends User {

    private Object info;

    public AuthenticationUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Object info) {
        super(username, password, authorities);
        this.info = info;
    }

    public AuthenticationUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}

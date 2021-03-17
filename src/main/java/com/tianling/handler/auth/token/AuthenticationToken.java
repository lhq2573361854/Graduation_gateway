package com.tianling.handler.auth.token;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/1/28 23:09
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String phone;
    private ArrayList<Object> info;

    public AuthenticationToken(Object principal, Object credentials, String phone) {
        super(principal, credentials);
        this.phone = phone;

    }

    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities,ArrayList<Object> info) {
        super(principal, credentials, authorities);
        this.info = info;
    }
}

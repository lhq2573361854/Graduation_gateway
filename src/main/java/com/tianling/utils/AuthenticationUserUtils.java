package com.tianling.utils;

import com.tianling.handler.auth.entities.AuthenticationUser;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple3;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 17:37
 */
@Component
public class AuthenticationUserUtils {
    public    Function<Tuple3<LinkedHashMap, LinkedHashMap, LinkedHashMap>, UserDetails> getTuple3UserDetailsFunction(String userPhone) {
        return data -> {
            String roleString = Arrays.stream(((String) data.getT2().get("userRole")).split(",")).map(name1 -> "ROLE_" + name1).collect(Collectors.joining(","));
            String authorityString = (String) data.getT3().get("userAuthority");
            String authorities = roleString + "," + authorityString;
            return new AuthenticationUser(userPhone, (String) data.getT1().get("userPass"), AuthorityUtils.commaSeparatedStringToAuthorityList(authorities), data.getT1());
        };
    }
}

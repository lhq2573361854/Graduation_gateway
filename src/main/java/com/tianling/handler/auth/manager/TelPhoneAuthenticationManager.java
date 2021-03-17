package com.tianling.handler.auth.manager;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tianling.common.ExceptionMessage;
import com.tianling.handler.auth.detail.service.MySqlReactiveUserDetailsServiceImpl;
import com.tianling.handler.auth.entities.AuthenticationUser;
import com.tianling.handler.auth.entities.TransformMessage;
import com.tianling.handler.auth.token.AuthenticationToken;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 21:06
 */
@Component
public class TelPhoneAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {
    @Resource
    ObjectMapper objectMapper;

    @Resource
    MySqlReactiveUserDetailsServiceImpl mySqlReactiveUserDetailsService;

    @SneakyThrows
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        AuthenticationToken authentication1 = (AuthenticationToken)authentication;
        String phone = authentication1.getPhone();
        if( StrUtil.isBlank(phone)){
            return Mono.error(new RuntimeException(ExceptionMessage.PARAMETERIZATION));
        }
        TransformMessage transformMessage = new TransformMessage();
        transformMessage.setName("phone");
        transformMessage.setValue(phone);
        return retrieveUser(objectMapper.writeValueAsString(transformMessage))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .map(u -> {
                    ArrayList<Object> list = new ArrayList<>();
                    list.add(((AuthenticationUser)u).getInfo());
                    return new AuthenticationToken(u, u.getPassword(), u.getAuthorities(),list);
                });

    }

    @Override
    protected Mono<UserDetails> retrieveUser(String s) {

        return mySqlReactiveUserDetailsService.findByUsername(s);
    }
}

package com.tianling.handler.auth.manager;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tianling.common.ExceptionMessage;
import com.tianling.handler.auth.detail.service.MySqlReactiveUserDetailsServiceImpl;
import com.tianling.handler.auth.entities.AuthenticationUser;
import com.tianling.handler.auth.entities.TransformMessage;
import com.tianling.handler.auth.token.AuthenticationToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author TianLing
 */
@Component("authenticationManager")
@Slf4j
public class AuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private MySqlReactiveUserDetailsServiceImpl mySqlReactiveUserDetailsService;

    @Resource
    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        AuthenticationToken authentication1 = (AuthenticationToken)authentication;
        Object username = authentication1.getPrincipal();
        Object password = authentication1.getCredentials();
        if(ObjectUtil.isNull(username) && ObjectUtil.isNull(password)){
            return Mono.error(new RuntimeException(ExceptionMessage.PARAMETERIZATION));
        }
        TransformMessage transformMessage = new TransformMessage();
        transformMessage.setName("username");
        transformMessage.setValue(username.toString());
        return retrieveUser(objectMapper.writeValueAsString(transformMessage))
                .filter(u -> passwordEncoder.matches(password.toString(),u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .flatMap(u -> {
                    boolean upgradeEncoding = mySqlReactiveUserDetailsService != null
                            && passwordEncoder.upgradeEncoding(u.getPassword());
                    if (upgradeEncoding) {
                        String newPassword = passwordEncoder.encode(password.toString());
                        return mySqlReactiveUserDetailsService.updatePassword(u, newPassword);
                    }
                    return Mono.just(u);
                }).flatMap(u2->{
                    LinkedHashMap info = (LinkedHashMap) (((AuthenticationUser) u2).getInfo());
                    return  mySqlReactiveUserDetailsService
                            .updateUserLoginTime((Integer) info.get("id"))
                            .flatMap(responseInfo -> {
                                LinkedHashMap data =  (LinkedHashMap)(responseInfo.getData());
                                info.put("recentlyTime",data.get("recentlyTime"));
                                info.put("previousTime",data.get("previousTime"));
                                return Mono.just(u2);
                            });
                })

                .map(u -> {
                    ArrayList<Object> list = new ArrayList<>();
                    list.add(((AuthenticationUser)u).getInfo());
                    return new AuthenticationToken(u, u.getPassword(), u.getAuthorities(),list);
                });
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return mySqlReactiveUserDetailsService.findByUsername(username);
    }
}
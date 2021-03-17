package com.tianling.handler.auth.detail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tianling.handler.auth.entities.TransformMessage;
import com.tianling.utils.AuthenticationUserUtils;
import com.tianling.utils.WebClientUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author TianLing
 */
@Component
@Slf4j
public class MySqlReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {
    @Resource
    private WebClientUtils webClientUtils;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    private AuthenticationUserUtils authenticationUserUtils;

    @SneakyThrows
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        TransformMessage transformMessage = objectMapper.readValue(username, TransformMessage.class);
        if(transformMessage.getName().equals("phone")){
            return findByPhone(transformMessage.getValue());
        }else{
            return findByName(transformMessage.getValue());
        }

    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        log.info("输 出 的密码是 {}", newPassword);
        com.tianling.entities.User user1 = new com.tianling.entities.User();
        user1.setUserName(user.getUsername());
        user1.setUserPass(newPassword);
        webClientUtils.postMethodReturnData("user/updateUserByUserName",user1);
        UserDetails build = User.withUsername(user.getUsername()).password(newPassword).authorities(user.getAuthorities()).build();
        return Mono.just(build);
    }



    public Mono<UserDetails> findByPhone(String userPhone) {
        return webClientUtils.getMethodReturnData("user/getUserByUserPhone/" + userPhone).flatMap(linkedHashMap -> {
            String userName = (String) linkedHashMap.get("userName");
            return Mono.zip(Mono.just(linkedHashMap)
                    ,webClientUtils.getMethodReturnData("role/getRoleByUserName/" + userName)
                    ,webClientUtils.getMethodReturnData("authority/getAuthorityByUserName/" + userName));
        }).map(authenticationUserUtils.getTuple3UserDetailsFunction(userPhone));
    }

    public Mono<UserDetails> findByName(String username) {
               return  Mono.zip(webClientUtils.getMethodReturnData("user/getUserByUserName/" + username),
                        webClientUtils.getMethodReturnData("role/getRoleByUserName/" + username),
                        webClientUtils.getMethodReturnData("authority/getAuthorityByUserName/" + username)).doOnError(Mono::error)
                        .map(authenticationUserUtils.getTuple3UserDetailsFunction(username));

    }

}
package com.tianling.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tianling.entities.ResponseInfo;
import com.tianling.entities.User;
import com.tianling.utils.WebClientUtils;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/25 11:55
 */
@RestController
public class UserController {
    @Resource
    WebClientUtils webClientUtils;
    @Resource
    PasswordEncoder passwordEncoder;

    @PostMapping(value = "/update/user",produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<ResponseInfo> updateUser(@RequestBody User user){
        return  Mono.just(user).map(user1 -> {
            if(ObjectUtil.isNotNull(user1.getUserPass())){
                user1.setUserPass(passwordEncoder.encode(user1.getUserPass()));
            }
            return user1;
        }).flatMap(user2-> webClientUtils.postMethodReturnData("user/updateUserById",user2));

    }

}

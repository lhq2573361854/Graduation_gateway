package com.tianling.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import com.tianling.entities.ResponseInfo;
import com.tianling.entities.User;
import com.tianling.handler.auth.entities.PhoneMessageResponseInfo;
import com.tianling.handler.phone.message.PhoneMessageClient;
import com.tianling.utils.HttpResponseMessageUtils;
import com.tianling.utils.WebClientUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/10 16:23
 */
@RestController
@Slf4j
@RequestMapping("/code")
public class ValidateCodeController {
    @Resource
    PhoneMessageClient phoneMessageClient;

    @Resource
    Producer producer;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    WebClientUtils webClientUtils;

    @Resource
    PasswordEncoder passwordEncoder;

    public static final String SESSION_VALIDATECODE_IMAGE="image_validateCode";

    public static final String SESSION_VALIDATECODE_PHONE="phone_validateCode";



    @GetMapping("/validate")
    public Mono<DataBuffer> imageValidateCode(ServerWebExchange serverWebExchange){
        String capText = producer.createText();
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        String code = capText.substring(capText.lastIndexOf("@") + 1);
        BufferedImage image = producer.createImage(capStr);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPEG", os);
        } catch (IOException e) {
            log.error("ImageIO write err", e);
            return Mono.error(e);
        }
       return  serverWebExchange.getSession().flatMap(webSession -> {
            webSession.getAttributes().put(SESSION_VALIDATECODE_IMAGE,code);
            return  Mono.just(serverWebExchange.getResponse().bufferFactory().wrap(os.toByteArray()));
        });
    }
    @GetMapping("/tel/{phone}")
    public Mono<ResponseInfo<String>> getTelPhoneCode(@PathVariable String phone, ServerWebExchange serverWebExchange){
        log.info("phone = {}",phone);
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 6; i++) {
            sb.append(RandomUtil.randomInt(0, 9));
        }
        log.info("code = {}",sb.toString());
        return  serverWebExchange.getSession().flatMap(webSession -> {
            webSession.getAttributes().put(SESSION_VALIDATECODE_PHONE,sb.toString());
            return  Mono.just(phoneMessageClient.sendMessage(phone,sb.toString()));
        }).log().flatMap(json-> {
            PhoneMessageResponseInfo phoneMessageResponseInfo;
            try {
                phoneMessageResponseInfo =  objectMapper.readValue(json, PhoneMessageResponseInfo.class);
            } catch (JsonProcessingException e) {
               return Mono.error(e);
            }
            if(phoneMessageResponseInfo.getCode().equals(0)){
                return HttpResponseMessageUtils.sendMessageSuccess();
            }
            return HttpResponseMessageUtils.sendMessageFailed();
        });
    }

    @PostMapping(value = "/addUser",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseInfo> addUser(@RequestBody User user ){
        return Mono.just(user)
                .map(user1->{
                    String encode = passwordEncoder.encode(user1.getUserPass());
                    user1.setUserPass(encode);
                    return user;
                }).flatMap(user2-> webClientUtils.postMethodReturnData("user/createUser",user2));
    }

    @SneakyThrows
    private <T> T  mapToEntity(MultiValueMap map, Class<T> clazz) {
        T t = ReflectUtil.newInstance(clazz);
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            if(!StrUtil.equals(field.getName(),"serialVersionUID")){
                ReflectUtil.setFieldValue(t,field,map.getFirst(field.getName()));
            }
        }
        return t;
    }
}

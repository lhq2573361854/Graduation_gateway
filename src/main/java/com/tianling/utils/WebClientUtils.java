package com.tianling.utils;

import cn.hutool.core.util.ObjectUtil;
import com.tianling.common.ExceptionMessage;
import com.tianling.entities.ResponseInfo;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 16:59
 */
@Component
public class WebClientUtils {
    private static final String BASENAME = "http://nacos-provider-client";

    @Resource
    private WebClient.Builder webClient;

    public Mono<LinkedHashMap> getMethodReturnData(String url) {
        return webClient.baseUrl(BASENAME)
                .build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ResponseInfo.class)
                .flatMap(getResponseInfoMonoFunction())
                .log()
                .map(ResponseInfo::getData)
                .cast(LinkedHashMap.class);
    }

    @SneakyThrows
    public Mono<ResponseInfo> postMethodReturnData(String url, Object obj) {
        MultiValueMap<String, String> stringObjectMap = entityToMap(obj);
        return webClient.baseUrl(BASENAME)
                .build()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(obj)
                .retrieve()
                .bodyToMono(ResponseInfo.class)
                .log()
                .flatMap(getResponseInfoMonoFunction());
    }

    private Function<ResponseInfo, Mono<? extends ResponseInfo>> getResponseInfoMonoFunction() {
        return responseInfo -> {
            if (responseInfo.getCode() == HttpStatus.OK.value()) {
                return Mono.just(responseInfo);
            } else {
                Object data = responseInfo.getData();
                String message;
                if (ObjectUtil.isNull(data)) {
                    message = ExceptionMessage.PARAMETERIZATION;
                } else {
                    message = (String) data;
                }
                return Mono.error(new RuntimeException(message));
            }
        };
    }

    private  MultiValueMap<String, String> entityToMap(Object object) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        for (Field field : object.getClass().getDeclaredFields()){
            try {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(object);
                if(ObjectUtil.isNotNull(o)){
                    map.add(field.getName(), o.toString());
                }
                field.setAccessible(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }



}

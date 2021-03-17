package com.tianling.handler.auth.filters;

import cn.hutool.core.util.StrUtil;
import com.tianling.common.PathVariables;
import com.tianling.controller.ValidateCodeController;
import com.tianling.exception.ValidateCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/9 17:19
 */
@Slf4j
@Component
public class ValidatorFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        Mono<WebSession> session = serverWebExchange.getSession();
        Mono<MultiValueMap<String, String>> formData = serverWebExchange.getFormData();

        if(StrUtil.contains(serverWebExchange.getRequest().getURI().toString(), PathVariables.LOGIN)){
            return Mono.zip(session,formData).flatMap(tuple->{
                String o = (String) tuple.getT1().getAttributes().get(ValidateCodeController.SESSION_VALIDATECODE_IMAGE);
                String first = tuple.getT2().getFirst(ValidateCodeController.SESSION_VALIDATECODE_IMAGE);
                if(!StrUtil.equals(o,first)){
                    return Mono.error(new ValidateCodeException());
                }
                return webFilterChain.filter(serverWebExchange);
            });
        }

        if(StrUtil.contains(serverWebExchange.getRequest().getURI().toString(), PathVariables.PHONEMESSAGE)){
            return Mono.zip(session,formData).flatMap(tuple->{
                String o = (String) tuple.getT1().getAttributes().get(ValidateCodeController.SESSION_VALIDATECODE_PHONE);
                String first = tuple.getT2().getFirst(ValidateCodeController.SESSION_VALIDATECODE_PHONE);
                if(!StrUtil.equals(o,first)){
                    return Mono.error(new ValidateCodeException());
                }
                return webFilterChain.filter(serverWebExchange);
            });
        }

        return webFilterChain.filter(serverWebExchange);
    }

}

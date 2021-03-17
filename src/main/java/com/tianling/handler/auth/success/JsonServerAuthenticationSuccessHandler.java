package com.tianling.handler.auth.success;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tianling.common.AuthenticationMessage;
import com.tianling.handler.auth.token.AuthenticationToken;
import com.tianling.utils.HttpResponseMessageUtils;
import com.tianling.utils.JwtUtils;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author TianLing
 */
@Component
@Slf4j
public class JsonServerAuthenticationSuccessHandler  implements ServerAuthenticationSuccessHandler {
    @Resource
    RedisTemplate redisTemplate;

    @Resource
    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        response.setStatusCode(HttpStatus.OK);

        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        String authorities = authentication.getAuthorities().stream().map(authentication1 -> authentication1.getAuthority()).filter(role-> !StrUtil.contains(role,"ROLE_")).collect(Collectors.joining(","));

        String roles = authentication.getAuthorities().stream().map(authentication1 -> authentication1.getAuthority()).filter(role-> StrUtil.contains(role,"ROLE_")).map(role2-> StrUtil.replace(role2,"ROLE_","")).collect(Collectors.joining(","));

        AuthenticationToken authentication2 = (AuthenticationToken) authentication;

        ArrayList<Object> lists = authentication2.getInfo();

        lists.add(authorities);

        lists.add(roles);

        String result = objectMapper.writeValueAsString(HttpResponseMessageUtils.authenticationSuccess(lists));

        DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(CharsetUtil.UTF_8));

        String token = JwtUtils.createToken(authentication.getName(), roles, authorities);

        token = StrUtil.addPrefixIfNot(token, AuthenticationMessage.AUTHENTICATIONCLINETTOKENPREFIX.getMessage());

        updateRedisData(token,authentication);

        ResponseCookie.ResponseCookieBuilder from = ResponseCookie.from(AuthenticationMessage.AUTHENTICATIONTOKENPREFIX.getMessage() +
                        AuthenticationMessage.AUTHENTICATIONTOKEN.getMessage()
                , token);

        ResponseCookie responseCookie = setResponseCookie(token, webFilterExchange.getExchange().getRequest().getURI().getHost());

        response.addCookie(responseCookie);

        return response.writeWith(Mono.just(buffer));

    }

    private ResponseCookie setResponseCookie(String token,String host){
        ResponseCookie.ResponseCookieBuilder from = ResponseCookie.from(AuthenticationMessage.AUTHENTICATIONTOKENPREFIX.getMessage() +
                        AuthenticationMessage.AUTHENTICATIONTOKEN.getMessage(), token);
        from.domain(host);

        from.path("/");

        from.httpOnly(Boolean.FALSE);

        from.maxAge(3600);

        return from.build();
    }

    private void updateRedisData(String token,Authentication authentication){
        redisTemplate.opsForValue().set(AuthenticationMessage.AUTHENTICATIONTOKEN.getMessage()+"_"+authentication.getName(),token,3600, TimeUnit.SECONDS);

    }
}

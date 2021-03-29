package com.tianling.handler.auth.filters;

import cn.hutool.core.util.StrUtil;
import com.tianling.common.AuthenticationMessage;
import com.tianling.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/1/25 16:30
 */
@Component
@Slf4j
public class TokenAuthenticationFilter implements WebFilter {
    @Value("#{'${webfilter.path}'.split(',')}")
    String[] paths;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String path = request.getURI().getPath();
        for (String s : paths) {
            if (StrUtil.contains(s, path)) {
                return chain.filter(exchange);
            }
        }

        String authToken = headers.getFirst(AuthenticationMessage.AUTHENTICATIONTOKENPREFIX.getMessage() + AuthenticationMessage.AUTHENTICATIONTOKEN.getMessage());
        if (!StrUtil.isBlank(authToken) || StrUtil.contains(authToken,AuthenticationMessage.AUTHENTICATIONCLINETTOKENPREFIX.getMessage())) {
            UsernamePasswordAuthenticationToken authenticationToken;
            String token = StrUtil.removePrefix(authToken, AuthenticationMessage.AUTHENTICATIONCLINETTOKENPREFIX.getMessage());
            exchange.getAttributes().put("token",token);
            authenticationToken = new UsernamePasswordAuthenticationToken(JwtUtils.getUsername(token),"",
                    Arrays.stream(JwtUtils.getUserAuthority(token).split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            return chain.filter(exchange)
                    .subscriberContext(context -> ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
        }else{
            return chain.filter(exchange);
        }


    }
}
